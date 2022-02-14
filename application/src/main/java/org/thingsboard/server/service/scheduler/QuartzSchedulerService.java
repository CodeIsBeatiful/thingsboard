package org.thingsboard.server.service.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.ThingsBoardThreadFactory;
import org.thingsboard.server.cluster.TbClusterService;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.OtaPackageInfo;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.id.*;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

/**
 * Created by blackstar on 01.13.22.
 */
@Service
@TbCoreComponent
@Slf4j
public class QuartzSchedulerService extends TbApplicationEventListener<PartitionChangeEvent> implements SchedulerService {

    private final TbClusterService clusterService;

    private final SchedulerJobService schedulerJobService;

    private final PartitionService partitionService;

    private final TenantService tenantService;

    private final DeviceService deviceService;

    private final OtaPackageService otaPackageService;

    private Scheduler jobScheduler;

    private ObjectMapper objectMapper = new ObjectMapper();

    private ListeningScheduledExecutorService queuedExecutor;

    final Queue<Set<TopicPartitionInfo>> subscribeQueue = new ConcurrentLinkedQueue<>();

    final ConcurrentMap<TopicPartitionInfo, Set<SchedulerJobId>> partitionedSchedulerJobs = new ConcurrentHashMap();

    final ConcurrentMap<SchedulerJobId, SchedulerJob> schedulerJobs = new ConcurrentHashMap();


    public QuartzSchedulerService(TbClusterService clusterService, SchedulerJobService schedulerJobService, PartitionService partitionService, TenantService tenantService, DeviceService deviceService, OtaPackageService otaPackageService) {
        this.clusterService = clusterService;
        this.schedulerJobService = schedulerJobService;
        this.partitionService = partitionService;
        this.tenantService = tenantService;
        this.deviceService = deviceService;
        this.otaPackageService = otaPackageService;
    }

    @PostConstruct
    public void init() {
        queuedExecutor = MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor(ThingsBoardThreadFactory.forName("quartz-scheduler-service")));
        log.info("Initializing Quartz Scheduler Service.");
        //todo can add some properties for new factory
        SchedulerFactory factory = new StdSchedulerFactory();
        try {
            jobScheduler = factory.getScheduler();
            jobScheduler.getContext().put("schedulerService",this);
            jobScheduler.start();
            log.info("Quartz Scheduler Service initialized.");
        } catch (SchedulerException e) {
            log.error("Quartz Scheduler Service start failed!", e);
        }

    }

    @PreDestroy
    public void destroy() {
        try {
            if (jobScheduler.isStarted()) {
                jobScheduler.shutdown();
            }
        } catch (SchedulerException e) {
            log.error("Quartz Scheduler Service shutdown failed!", e);
        }

    }


    @Override
    public void add(SchedulerJobInfo schedulerJobInfo) {
        processSchedulerJobInfo(schedulerJobInfo, true, false, false);
    }


    @Override
    public void update(SchedulerJobInfo schedulerJobInfo) {
        processSchedulerJobInfo(schedulerJobInfo, false, true, false);
    }

    @Override
    public void delete(SchedulerJobInfo schedulerJobInfo) {
        processSchedulerJobInfo(schedulerJobInfo, false, false, true);
    }

    private void processSchedulerJobInfo(SchedulerJobInfo schedulerJobInfo, boolean added, boolean updated, boolean deleted) {
        //todo can optimization. if schedulerJobId in our partitions，process it.
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
        this.clusterService.pushMsgToCore(schedulerJobInfo.getTenantId(), schedulerJobInfo.getId(), toCoreMsg, new TbQueueCallback() {
            public void onSuccess(TbQueueMsgMetadata metadata) {
                log.info("Scheduler Service pushMsgToCore Success");
            }

            public void onFailure(Throwable t) {
                log.error("Scheduler Service pushMsgToCore failed", t);
            }
        });
    }

    @Override
    public void onQueueMsg(TransportProtos.SchedulerServiceMsgProto proto, TbCallback callback) {
        log.debug("onQueueMsg proto {}", proto);
        TenantId tenantId = new TenantId(new UUID(proto.getTenantIdMSB(), proto.getTenantIdLSB()));
        SchedulerJobId schedulerJobId = new SchedulerJobId(new UUID(proto.getSchedulerJobIdMSB(), proto.getSchedulerJobIdLSB()));
        TopicPartitionInfo tpi = this.partitionService.resolve(ServiceType.TB_CORE, tenantId, schedulerJobId);
        if (proto.getDeleted()) {
            removeJob(tpi, schedulerJobId);
        } else {
            addJob(tpi, schedulerJobId);
        }
        callback.onSuccess();
    }

    @Override
    protected void onTbApplicationEvent(PartitionChangeEvent partitionChangeEvent) {
        if (ServiceType.TB_CORE.equals(partitionChangeEvent.getServiceType())) {
            log.debug("onTbApplicationEvent ServiceType is TB_CORE, processing queue {}", partitionChangeEvent);
            subscribeQueue.add(partitionChangeEvent.getPartitions());
            queuedExecutor.submit(this::pollInitStateFromDB);
        }
    }

    private Set<TopicPartitionInfo> getLatestPartitionsFromQueue() {
        log.debug("getLatestPartitionsFromQueue, queue size {}", Integer.valueOf(this.subscribeQueue.size()));
        Set<TopicPartitionInfo> partitions = null;
        while (!this.subscribeQueue.isEmpty()) {
            partitions = this.subscribeQueue.poll();
            log.debug("polled from the queue partitions {}", partitions);
        }
        log.debug("getLatestPartitionsFromQueue, partitions {}", partitions);
        return partitions;
    }

    private void pollInitStateFromDB() {
        Set<TopicPartitionInfo> partitions = getLatestPartitionsFromQueue();
        if (partitions == null) {
            log.info("Scheduler service. Nothing to do. partitions is null");
        } else {
            initStateFromDB(partitions);
        }
    }

    private void initStateFromDB(Set<TopicPartitionInfo> partitions) {
        try {
            log.info("Scheduler service init state beginning.");
            Set<TopicPartitionInfo> addedPartitions = new HashSet<>(partitions);
            addedPartitions.removeAll(this.partitionedSchedulerJobs.keySet());
            log.trace("Check need add partitions {}", addedPartitions);
            Set<TopicPartitionInfo> removedPartitions = new HashSet<>(this.partitionedSchedulerJobs.keySet());
            removedPartitions.removeAll(partitions);
            log.trace("Check need remove partitions {}", removedPartitions);
            addedPartitions.forEach(tpi -> {
                this.partitionedSchedulerJobs.computeIfAbsent(tpi, key -> ConcurrentHashMap.newKeySet());
            });
            removedPartitions.forEach(partition -> {
                Set<SchedulerJobId> schedulerJobIds = Optional.ofNullable(this.partitionedSchedulerJobs.remove(partition)).orElseGet(Collections::emptySet);
                log.trace("removing partition {}, schedulerJobIds found {}", partition, schedulerJobIds);
                schedulerJobIds.forEach(schedulerJobId -> {
                    removeJob(partition, schedulerJobId);
                });
            });
            if (!addedPartitions.isEmpty()) {
                List<Tenant> tenants = tenantService.findTenants(new PageLink(Integer.MAX_VALUE)).getData();
                for (Tenant tenant : tenants) {
                    //todo scheduler jobs in a tenant must less than 1000
                    List<SchedulerJobInfo> schedulerJobInfos = schedulerJobService.findSchedulerJobsByTenantId(tenant.getId(), new PageLink(Integer.MAX_VALUE)).getData();
                    for (SchedulerJobInfo schedulerJobInfo : schedulerJobInfos) {
                        TopicPartitionInfo tpi = this.partitionService.resolve(ServiceType.TB_CORE, tenant.getId(), schedulerJobInfo.getId());
                        if (addedPartitions.contains(tpi)) {
                            addJob(tpi, schedulerJobInfo.getId());
                        }
                    }
                }
            }
            log.info("Scheduler service init state Ending.");
        } catch (Throwable t) {
            log.warn("Failed to init state from DB", t);
        }
    }

    private void removeJob(TopicPartitionInfo partition, SchedulerJobId schedulerJobId) {
        try {
            this.partitionedSchedulerJobs.computeIfPresent(partition, (key, value) -> {
                value.remove(schedulerJobId);
                return value;
            });
            this.schedulerJobs.remove(schedulerJobId);
            jobScheduler.deleteJob(new JobKey(schedulerJobId.getId().toString()));
        } catch (SchedulerException e) {
            log.error("Delete job failed, schedulerJobId: {}!", schedulerJobId);
        }
    }

    private void addJob(TopicPartitionInfo partition, SchedulerJobId schedulerJobId) {

        SchedulerJob schedulerJob = this.schedulerJobService.findSchedulerJobById(null, schedulerJobId);
        if (schedulerJob != null) {
            this.partitionedSchedulerJobs.computeIfPresent(partition, (key, value) -> {
                value.add(schedulerJobId);
                return value;
            });
            this.schedulerJobs.put(schedulerJobId, schedulerJob);
            try {
                JobDetail detail = getJobDetail(schedulerJob);
                Trigger trigger = getTrigger(schedulerJob);
                jobScheduler.scheduleJob(detail, Collections.singleton(trigger), true);
            } catch (SchedulerException e) {
                log.error("Schedule job failed", e);
            } catch (JsonProcessingException e) {
                log.error("Scheduler Job configuration is not json", e);
            }
        } else {
            log.warn("SchedulerJob can not find. SchedulerJobId: [{}]", schedulerJobId);
        }

    }

    private JobDetail getJobDetail(SchedulerJob schedulerJob) throws JsonProcessingException {
        JobKey jobKey = new JobKey(schedulerJob.getId().toString());
        return JobBuilder
                .newJob(QuartzSchedulerJob.class)
                .withIdentity(jobKey)
                .build();
    }

    private Trigger getTrigger(SchedulerJob schedulerJob) {
        JsonNode scheduler = schedulerJob.getScheduler();
        String timezone = scheduler.get("timezone").asText();
        long startTime = scheduler.get("startTime").asLong();
        JsonNode repeatJsonNode = scheduler.get("repeat");
        TriggerKey triggerKey = new TriggerKey(schedulerJob.getId().toString());
        if (repeatJsonNode == null) {
            return TriggerBuilder
                    .newTrigger()
                    .startAt(new Date(startTime))
                    .withIdentity(triggerKey)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0).withIntervalInSeconds(60))
                    .build();

        } else {
            String repeatType = repeatJsonNode.get("type").asText();
            long endTime = repeatJsonNode.get("endTime").asLong();
            ScheduleBuilder scheduleBuilder = null;
            switch (repeatType) {
                case "DAILY":
                    scheduleBuilder = CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withMisfireHandlingInstructionDoNothing().withIntervalInDays(1);
                    break;
                case "WEEKLY":
                    Set repeatDays = objectMapper.convertValue(repeatJsonNode.get("repeatDays"), Set.class);
                    scheduleBuilder = DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().onDaysOfTheWeek(repeatDays);
                    break;
                case "MONTHLY":
                    scheduleBuilder = CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withMisfireHandlingInstructionDoNothing().withIntervalInMonths(1);
                    break;
                case "YEARLY":
                    scheduleBuilder = CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withMisfireHandlingInstructionDoNothing().withIntervalInYears(1);
                    break;
                case "TIMER":
                    SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule().repeatForever();
                    int repeatInterval = repeatJsonNode.get("repeatInterval").asInt();
                    String timeUnit = repeatJsonNode.get("timeUnit").asText();
                    switch (timeUnit) {
                        case "SECONDS":
                            simpleScheduleBuilder = simpleScheduleBuilder.withIntervalInSeconds(repeatInterval);
                            break;
                        case "MINUTES":
                            simpleScheduleBuilder = simpleScheduleBuilder.withIntervalInMinutes(repeatInterval);
                            break;
                        case "HOURS":
                            simpleScheduleBuilder = simpleScheduleBuilder.withIntervalInHours(repeatInterval);
                            break;
                        default:
                            //must not here
                            break;
                    }
                    scheduleBuilder = simpleScheduleBuilder;
                    break;
                case "CRON":
                    //todo
                    break;
                default:
                    //must not here
                    break;
            }
            return TriggerBuilder
                    .newTrigger()
                    .startAt(new Date(startTime))
                    .endAt(new Date(endTime))
                    .withIdentity(triggerKey)
                    .withSchedule(scheduleBuilder)
                    .build();

        }
    }

    public void process(SchedulerJobId schedulerJobId) {
        SchedulerJob schedulerJob = this.schedulerJobService.findSchedulerJobById(TenantId.SYS_TENANT_ID, schedulerJobId);
        try {
            if (schedulerJob != null) {
                JsonNode configuration = schedulerJob.getConfiguration();
                EntityId entityId = getEntityId(schedulerJob, configuration);
                String type = schedulerJob.getType();
                if (type.equals("updateFirmware") || type.equals("updateSoftware")) {
                    //check firmware exist
                    OtaPackageId otaPackageId = getOtaPackageId(configuration);
                    OtaPackageInfo otaPackageInfo = this.otaPackageService.findOtaPackageInfoById(TenantId.SYS_TENANT_ID, otaPackageId);
                    if(otaPackageInfo == null) {
                        log.warn("Can't find OtaPackage , OtaPackageId:[{}] !", otaPackageId);
                        return ;
                    }
                    if(entityId.getEntityType() != EntityType.DEVICE) {
                        log.warn("Ota only supports Entity Device now ! , Entity Type:[{}]", entityId.getEntityType().name());
                        return ;
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
                    log.debug("Push message to rule engine tenantId [{}], entityId [{}], tbMsg [{}]", new Object[] { schedulerJob.getTenantId(), schedulerJob.getId(), tbMsg });
                    this.clusterService.pushMsgToRuleEngine(schedulerJob.getTenantId(), schedulerJob.getId(), tbMsg, null);
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
        return (OtaPackageId)EntityIdFactory.getByTypeAndId(jsonNode.get("entityType").asText(), jsonNode.get("id").asText());
    }

    private String getMsgType(JsonNode configuration) {
        return configuration.get("msgType").asText();
    }

    private String getMsgBody(JsonNode configuration) throws JsonProcessingException {
        return this.objectMapper.writeValueAsString(configuration.get("msgBody"));
    }

    private EntityId getEntityId(SchedulerJob schedulerJob, JsonNode configuration) {
        EntityId entityId;
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
