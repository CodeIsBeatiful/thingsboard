/**
 * Copyright © 2016-2022 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.service.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.ThingsBoardThreadFactory;
import org.thingsboard.server.cluster.TbClusterService;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.OtaPackageInfo;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.EntityIdFactory;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.common.data.id.SchedulerJobId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.scheduler.SchedulerJob;
import org.thingsboard.server.common.data.scheduler.SchedulerJobInfo;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgDataType;
import org.thingsboard.server.common.msg.TbMsgMetaData;
import org.thingsboard.server.common.msg.queue.ServiceType;
import org.thingsboard.server.common.msg.queue.TbCallback;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.ota.OtaPackageService;
import org.thingsboard.server.dao.scheduler.SchedulerJobService;
import org.thingsboard.server.dao.tenant.TenantService;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.TbQueueCallback;
import org.thingsboard.server.queue.TbQueueMsgMetadata;
import org.thingsboard.server.queue.discovery.PartitionService;
import org.thingsboard.server.queue.discovery.TbApplicationEventListener;
import org.thingsboard.server.queue.discovery.event.PartitionChangeEvent;
import org.thingsboard.server.queue.util.TbCoreComponent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by blackstar on 01.13.22.
 */
@Service
@TbCoreComponent
@Slf4j
public class DefaultSchedulerService extends TbApplicationEventListener<PartitionChangeEvent> implements SchedulerService {

    private final TbClusterService clusterService;

    private final SchedulerJobService schedulerJobService;

    private final PartitionService partitionService;

    private final TenantService tenantService;

    private final DeviceService deviceService;

    private final OtaPackageService otaPackageService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private ListeningScheduledExecutorService queuedExecutor;

    final Queue<Set<TopicPartitionInfo>> subscribeQueue = new ConcurrentLinkedQueue<>();

    final ConcurrentMap<SchedulerJobId, SchedulerJobContext> schedulerJobContexts = new ConcurrentHashMap();

    private final ConcurrentMap<TopicPartitionInfo, Set<TenantId>> partitionedTenants = new ConcurrentHashMap();


    public DefaultSchedulerService(TbClusterService clusterService, SchedulerJobService schedulerJobService, PartitionService partitionService, TenantService tenantService, DeviceService deviceService, OtaPackageService otaPackageService) {
        this.clusterService = clusterService;
        this.schedulerJobService = schedulerJobService;
        this.partitionService = partitionService;
        this.tenantService = tenantService;
        this.deviceService = deviceService;
        this.otaPackageService = otaPackageService;
    }

    @PostConstruct
    public void init() {
        queuedExecutor = MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor(ThingsBoardThreadFactory.forName("default-scheduler-service")));
        log.info("Default Scheduler Service initialized.");

    }

    @PreDestroy
    public void destroy() {
        queuedExecutor.shutdownNow();
        log.info("Default Scheduler Service destroyed.");
    }

    @Override
    protected void onTbApplicationEvent(PartitionChangeEvent partitionChangeEvent) {
        if (ServiceType.TB_CORE.equals(partitionChangeEvent.getServiceType())) {
            log.debug("onTbApplicationEvent ServiceType is TB_CORE, processing queue {}", partitionChangeEvent);
            subscribeQueue.add(partitionChangeEvent.getPartitions());
            queuedExecutor.submit(this::pollInitStateFromDB);
        }
    }

    @Override
    public void add(SchedulerJobInfo schedulerJobInfo) {
        process(schedulerJobInfo, true, false, false);
    }


    @Override
    public void update(SchedulerJobInfo schedulerJobInfo) {
        process(schedulerJobInfo, false, true, false);
    }

    @Override
    public void delete(SchedulerJobInfo schedulerJobInfo) {
        process(schedulerJobInfo, false, false, true);
    }

    @Override
    public void onQueueMsg(TransportProtos.SchedulerServiceMsgProto proto, TbCallback callback) {
        log.debug("onQueueMsg proto {}", proto);
        TenantId tenantId = new TenantId(new UUID(proto.getTenantIdMSB(), proto.getTenantIdLSB()));
        SchedulerJobId schedulerJobId = new SchedulerJobId(new UUID(proto.getSchedulerJobIdMSB(), proto.getSchedulerJobIdLSB()));
        if (proto.getDeleted()) {
            removeJob(schedulerJobId);
        } else {
            addJob(tenantId, schedulerJobId);
        }
        callback.onSuccess();
    }

    private void process(SchedulerJobInfo schedulerJobInfo, boolean added, boolean updated, boolean deleted) {
        TopicPartitionInfo tpi = this.partitionService.resolve(ServiceType.TB_CORE, schedulerJobInfo.getTenantId(), schedulerJobInfo.getTenantId());
        if (partitionedTenants.containsKey(tpi)) {
            if (deleted) {
                removeJob(schedulerJobInfo.getId());
            } else {
                addJob(schedulerJobInfo.getTenantId(), schedulerJobInfo.getId());
            }
        } else {
            TransportProtos.SchedulerServiceMsgProto.Builder builder = TransportProtos.SchedulerServiceMsgProto.newBuilder();
            builder.setTenantIdMSB(schedulerJobInfo.getTenantId().getId().getMostSignificantBits());
            builder.setTenantIdLSB(schedulerJobInfo.getTenantId().getId().getLeastSignificantBits());
            builder.setSchedulerJobIdMSB(schedulerJobInfo.getId().getId().getMostSignificantBits());
            builder.setSchedulerJobIdLSB(schedulerJobInfo.getId().getId().getLeastSignificantBits());
            builder.setAdded(added);
            builder.setUpdated(updated);
            builder.setDeleted(deleted);
            TransportProtos.SchedulerServiceMsgProto msg = builder.build();
            log.trace("Scheduler service msg {}", msg);
            TransportProtos.ToCoreMsg toCoreMsg = TransportProtos.ToCoreMsg.newBuilder().setSchedulerServiceMsg(msg).build();
            log.trace("toCoreMsg.hasSchedulerServiceMsg() {} toCoreMsg {}", Boolean.valueOf(toCoreMsg.hasSchedulerServiceMsg()), toCoreMsg);
            this.clusterService.pushMsgToCore(schedulerJobInfo.getTenantId(), schedulerJobInfo.getTenantId(), toCoreMsg, new TbQueueCallback() {
                public void onSuccess(TbQueueMsgMetadata metadata) {
                    log.trace("Scheduler Service pushMsgToCore success tenantId {}, jobId {}, added {}, updated {}, deleted {}"
                            , schedulerJobInfo.getTenantId(), schedulerJobInfo.getId(), added, updated, deleted);
                }

                public void onFailure(Throwable t) {
                    log.trace("Scheduler Service pushMsgToCore failed tenantId {}, jobId {}, added {}, updated {}, deleted {}"
                            , schedulerJobInfo.getTenantId(), schedulerJobInfo.getId(), added, updated, deleted);
                }
            });
        }
    }

    private void pollInitStateFromDB() {
        Set<TopicPartitionInfo> partitions = getLatestPartitions();
        if (partitions == null) {
            log.info("Scheduler service. Nothing to do. partitions is null");
        } else {
            initStateFromDB(partitions);
        }
    }

    private Set<TopicPartitionInfo> getLatestPartitions() {
        log.debug("getLatestPartitionsFromQueue, queue size {}", subscribeQueue.size());
        Set<TopicPartitionInfo> partitions = null;
        while (!subscribeQueue.isEmpty()) {
            partitions = subscribeQueue.poll();
            log.debug("polled from the queue partitions {}", partitions);
        }
        log.debug("getLatestPartitionsFromQueue, partitions {}", partitions);
        return partitions;
    }

    private void initStateFromDB(Set<TopicPartitionInfo> partitions) {
        try {
            log.info("Scheduler service init state beginning.");
            Set<TopicPartitionInfo> addedPartitions = new HashSet<>(partitions);
            addedPartitions.removeAll(this.partitionedTenants.keySet());
            log.trace("Check need add partitions {}", addedPartitions);
            Set<TopicPartitionInfo> removedPartitions = new HashSet<>(this.partitionedTenants.keySet());
            removedPartitions.removeAll(partitions);
            log.trace("Check need remove partitions {}", removedPartitions);
            addedPartitions.forEach(tpi -> {
                this.partitionedTenants.computeIfAbsent(tpi, key -> ConcurrentHashMap.newKeySet());
            });
            removedPartitions.forEach(partition -> {
                Set<TenantId> tenantIds = Optional.ofNullable(this.partitionedTenants.remove(partition)).orElseGet(Collections::emptySet);
                tenantIds.forEach(tenantId -> {
                    log.info("removing partition {} for tenantId {}", partition, tenantId);
                    removeJobInTenant(tenantId);
                });
            });
            if (!addedPartitions.isEmpty()) {
                List<Tenant> tenants = tenantService.findTenants(new PageLink(Integer.MAX_VALUE)).getData();
                for (Tenant tenant : tenants) {
                    TopicPartitionInfo tpi = this.partitionService.resolve(ServiceType.TB_CORE, tenant.getId(), tenant.getId());
                    if (addedPartitions.contains(tpi)) {
                        this.partitionedTenants.computeIfAbsent(tpi, (key) -> ConcurrentHashMap.newKeySet()).add(tenant.getId());
                        addJobsInTenant(tenant.getId());
                    }
                }
            }
            log.info("Scheduler service init state Ending.");
        } catch (Throwable t) {
            log.warn("Failed to init state from DB", t);
        }
    }

    private void addJobsInTenant(TenantId tenantId) {
        // todo scheduler jobs in a tenant must less than 1000
        // use property to limit this
        List<SchedulerJob> schedulerJobs = this.schedulerJobService.findSchedulerJobsByTenantId(tenantId, new PageLink(Integer.MAX_VALUE)).getData();
        for (SchedulerJob schedulerJob : schedulerJobs) {
            SchedulerJobContext context = new SchedulerJobContext(schedulerJob);
            this.scheduleNext(System.currentTimeMillis(), context);
            this.schedulerJobContexts.put(schedulerJob.getId(), context);
        }
    }

    private void removeJobInTenant(TenantId tenantId) {
        List<SchedulerJob> schedulerJobs = this.schedulerJobService.findSchedulerJobsByTenantId(tenantId, new PageLink(Integer.MAX_VALUE)).getData();
        if (schedulerJobs != null) {
            for (SchedulerJob schedulerJob : schedulerJobs) {
                this.removeJob(schedulerJob.getId());
            }
        }
    }

    private void addJob(TenantId tenantId, SchedulerJobId schedulerJobId) {
        SchedulerJob schedulerJob = this.schedulerJobService.findSchedulerJobById(tenantId, schedulerJobId);
        if (schedulerJob != null) {
            SchedulerJobContext oldContext = this.schedulerJobContexts.get(schedulerJobId);
            if (oldContext != null && oldContext.getScheduledFuture() != null) {
                // waiting for job complete
                oldContext.getScheduledFuture().cancel(false);
            }
            SchedulerJobContext context = new SchedulerJobContext(schedulerJob);
            scheduleNext(System.currentTimeMillis(), context);
            this.schedulerJobContexts.put(schedulerJobId, context);
        } else {
            log.warn("SchedulerJob can not find. SchedulerJobId: [{}]", schedulerJobId);
        }
    }

    private void scheduleNext(long ts, SchedulerJobContext context) {
        long nextTime = context.getNextTime(ts);
        if (nextTime != 0) {
            context.setScheduledFuture(this.queuedExecutor.schedule(() ->
                            this.processMsg(context.getSchedulerJob().getTenantId(), context.getSchedulerJob().getId())
                    , nextTime - ts, TimeUnit.MILLISECONDS));
        }
    }

    private void removeJob(SchedulerJobId schedulerJobId) {
        SchedulerJobContext context = this.schedulerJobContexts.get(schedulerJobId);
        if (context != null && context.getScheduledFuture() != null) {
            // waiting for job complete
            context.getScheduledFuture().cancel(false);
        }
        this.schedulerJobContexts.remove(schedulerJobId);
    }

    private void processMsg(TenantId tenantId, SchedulerJobId schedulerJobId) {
        SchedulerJob schedulerJob = this.schedulerJobService.findSchedulerJobById(tenantId, schedulerJobId);
        try {
            if (schedulerJob != null) {
                JsonNode configuration = schedulerJob.getConfiguration();
                EntityId entityId = getEntityId(schedulerJob, configuration);
                String type = schedulerJob.getType();
                if (type.equals("updateFirmware") || type.equals("updateSoftware")) {
                    //check firmware exist
                    OtaPackageId otaPackageId = getOtaPackageId(configuration);
                    OtaPackageInfo otaPackageInfo = this.otaPackageService.findOtaPackageInfoById(TenantId.SYS_TENANT_ID, otaPackageId);
                    if (otaPackageInfo == null) {
                        log.warn("Can't find OtaPackage , OtaPackageId:[{}] !", otaPackageId);
                        return;
                    }
                    if (entityId.getEntityType() != EntityType.DEVICE) {
                        log.warn("Ota only supports Entity Device now ! , Entity Type:[{}]", entityId.getEntityType().name());
                        return;
                    }
                    Device device = this.deviceService.findDeviceById(TenantId.SYS_TENANT_ID, (DeviceId) entityId);
                    if (type.equals("updateFirmware")) {
                        device.setFirmwareId(otaPackageId);
                    } else {
                        device.setSoftwareId(otaPackageId);
                    }
                    this.deviceService.saveDevice(device);
                } else {
                    TbMsgMetaData tbMsgMetaData = getTbMsgMetaData(schedulerJob, configuration);
                    String msgType = getMsgType(configuration);
                    String msgBody = getMsgBody(configuration);
                    TbMsg tbMsg = TbMsg.newMsg(msgType, entityId, tbMsgMetaData, TbMsgDataType.JSON, msgBody);
                    log.debug("Push message to rule engine tenantId [{}], entityId [{}], tbMsg [{}]", new Object[]{schedulerJob.getTenantId(), schedulerJob.getId(), tbMsg});
                    this.clusterService.pushMsgToRuleEngine(schedulerJob.getTenantId(), schedulerJob.getId(), tbMsg, null);
                }
                SchedulerJobContext context = this.schedulerJobContexts.get(schedulerJobId);
                if (context != null) {
                    this.scheduleNext(System.currentTimeMillis(), context);
                }
            } else {
                log.warn("Scheduler job id:[{}] can't find", schedulerJobId);
            }
        } catch (JsonProcessingException e) {
            log.error("Scheduler job id [{}] body can't format to string", schedulerJobId, e);
        }
    }

    private OtaPackageId getOtaPackageId(JsonNode configuration) {
        JsonNode jsonNode = configuration.get("msgBody");
        return (OtaPackageId) EntityIdFactory.getByTypeAndId(jsonNode.get("entityType").asText(), jsonNode.get("id").asText());
    }

    private String getMsgType(JsonNode configuration) {
        return configuration.get("msgType").asText();
    }

    private String getMsgBody(JsonNode configuration) throws JsonProcessingException {
        return this.objectMapper.writeValueAsString(configuration.get("msgBody"));
    }

    private EntityId getEntityId(SchedulerJob schedulerJob, JsonNode configuration) {
        EntityId entityId = null;
        JsonNode jsonNode = configuration.get("originatorId");
        if (jsonNode != null) {
            entityId = EntityIdFactory.getByTypeAndId(jsonNode.get("entityType").asText(), jsonNode.get("id").asText());
        } else {
            entityId = schedulerJob.getId();
        }
        return entityId;
    }

    private TbMsgMetaData getTbMsgMetaData(SchedulerJob schedulerJob, JsonNode configuration) {
        HashMap<String, String> metaData = new HashMap<>();
        if (configuration.has("metadata") && !configuration.get("metadata").isNull()) {
            for (Iterator<Map.Entry<String, JsonNode>> it = configuration.get("metadata").fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> kv = it.next();
                metaData.put(kv.getKey(), ((JsonNode) kv.getValue()).asText());
            }
        } else {
            metaData.put("jobName", schedulerJob.getName());
        }
        return new TbMsgMetaData(metaData);
    }


}
