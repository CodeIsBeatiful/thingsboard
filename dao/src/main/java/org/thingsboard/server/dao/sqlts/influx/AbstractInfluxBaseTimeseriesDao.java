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
package org.thingsboard.server.dao.sqlts.influx;

import com.google.common.collect.Lists;
import com.influxdb.client.AuthorizationsApi;
import com.influxdb.client.BucketsApi;
import com.influxdb.client.BucketsQuery;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.OrganizationsApi;
import com.influxdb.client.OrganizationsQuery;
import com.influxdb.client.UsersApi;
import com.influxdb.client.domain.Authorization;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.Organization;
import com.influxdb.client.domain.Permission;
import com.influxdb.client.domain.PermissionResource;
import com.influxdb.client.domain.User;
import com.influxdb.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.thingsboard.common.util.ThingsBoardThreadFactory;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.BasicTsKvEntry;
import org.thingsboard.server.common.data.kv.BooleanDataEntry;
import org.thingsboard.server.common.data.kv.DoubleDataEntry;
import org.thingsboard.server.common.data.kv.JsonDataEntry;
import org.thingsboard.server.common.data.kv.KvEntry;
import org.thingsboard.server.common.data.kv.LongDataEntry;
import org.thingsboard.server.common.data.kv.StringDataEntry;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.dao.influx.InfluxDriverOptions;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
public class AbstractInfluxBaseTimeseriesDao {

    protected static final String DESC_ORDER = "DESC";

    protected static final long RECONNECT_SECOND_INTERVAL = 3;

    @Autowired
    private InfluxDriverOptions driverOptions;

    protected InfluxDBClient influxDBClient;

    protected ExecutorService readResultsProcessingExecutor;

    protected Organization organization;

    protected Bucket bucket;

    protected String token;

    @PostConstruct
    public void init() throws Exception {
        readResultsProcessingExecutor = Executors.newCachedThreadPool(ThingsBoardThreadFactory.forName("influx-callback"));
        InfluxDBClient adminInfluxDBClient = InfluxDBClientFactory.create(
                driverOptions.getUrl(), driverOptions.getUsername(), driverOptions.getPassword().toCharArray());
        while (!adminInfluxDBClient.ping()) {
            log.warn("Can't connect to influxdb [{}] [{}] [{}]",
                    driverOptions.getUrl(), driverOptions.getUsername(), driverOptions.getPassword());
            TimeUnit.SECONDS.sleep(RECONNECT_SECOND_INTERVAL);
        }
        // pre create org and bucket
        try {
            initEnv(adminInfluxDBClient);
        } catch (Exception e) {
            throw new RuntimeException("Check influxdb environment failed", e);
        }
        influxDBClient = InfluxDBClientFactory.create(driverOptions.getUrl(), token.toCharArray());
    }

    @PreDestroy
    public void stop() {
        if (influxDBClient != null) {
            influxDBClient.close();
        }
        if (readResultsProcessingExecutor != null) {
            readResultsProcessingExecutor.shutdownNow();
        }
    }

    private void initEnv(InfluxDBClient adminInfluxDBClient) {
        UsersApi usersApi = adminInfluxDBClient.getUsersApi();
        User me = usersApi.me();
        organization = findOrCreateOrg(me, adminInfluxDBClient);
        bucket = findOrCreateBucket(organization, adminInfluxDBClient);
        Authorization authorization = findOrCreateAuth(organization, adminInfluxDBClient);
        token = authorization.getToken();
    }

    private Organization findOrCreateOrg(User me, InfluxDBClient adminInfluxDBClient) {
        Organization organization = null;
        OrganizationsApi organizationsApi = adminInfluxDBClient.getOrganizationsApi();
        OrganizationsQuery organizationsQuery = new OrganizationsQuery();
        organizationsQuery.setOrg(driverOptions.getOrgName());
        organizationsQuery.setUserID(me.getId());
        try {
            organization = organizationsApi.findOrganizations(organizationsQuery).get(0);
        } catch (NotFoundException e) {
            organization = organizationsApi.createOrganization(driverOptions.getOrgName());
            log.info("Create InfluxDB organization [{}] successful.", organization);
        }
        return organization;
    }

    private Bucket findOrCreateBucket(Organization organization, InfluxDBClient adminInfluxDBClient) {
        Bucket bucket = null;
        BucketsApi bucketsApi = adminInfluxDBClient.getBucketsApi();
        BucketsQuery bucketsQuery = new BucketsQuery();
        bucketsQuery.setOrg(driverOptions.getOrgName());
        bucketsQuery.setName(driverOptions.getBucketName());
        try {
            bucket = bucketsApi.findBuckets(bucketsQuery).get(0);
        } catch (NotFoundException e) {
            bucket = bucketsApi.createBucket(driverOptions.getBucketName(), organization);
            log.info("Create InfluxDB bucket [{}] in organization [{}] successful.", bucket, organization.getName());
        }
        return bucket;
    }

    private Authorization findOrCreateAuth(Organization organization, InfluxDBClient adminInfluxDBClient) {
        Authorization authorization = null;
        AuthorizationsApi authorizationsApi = adminInfluxDBClient.getAuthorizationsApi();
        try {
            List<Authorization> authorizations = authorizationsApi.findAuthorizationsByOrgID(organization.getId());
            authorizations = authorizations.stream()
                    .filter(auth -> auth.getDescription().equals(driverOptions.getTokenName()))
                    .collect(Collectors.toList());
            if (authorizations.size() == 0) {
                authorization = createAuthorization(organization, authorizationsApi);
                log.info("Create InfluxDB authorization [{}] in organization [{}] successful.", authorization, organization);
            } else {
                authorization = authorizations.get(0);
            }
        } catch (NotFoundException e) {
            authorization = createAuthorization(organization, authorizationsApi);
            log.info("Create InfluxDB authorization [{}] in organization [{}] successful.", authorization, organization);
        }
        return authorization;
    }

    private Authorization createAuthorization(Organization organization, AuthorizationsApi authorizationsApi) {
        Authorization authorization = new Authorization();
        authorization.setOrgID(organization.getId());
        authorization.setDescription(driverOptions.getTokenName());

        PermissionResource resource = new PermissionResource();
        resource.setOrgID(organization.getId());
        resource.setType(PermissionResource.TYPE_BUCKETS);
        // Read permission
        Permission read = new Permission();
        read.setResource(resource);
        read.setAction(Permission.ActionEnum.READ);
        // Write permission
        Permission write = new Permission();
        write.setResource(resource);
        write.setAction(Permission.ActionEnum.WRITE);
        authorization.setPermissions(Lists.newArrayList(read, write));

        return authorizationsApi.createAuthorization(authorization);
    }

    protected String getOrgName() {
        return organization.getName();
    }

    protected String getBucketName() {
        return bucket.getName();
    }

    protected TsKvEntry convertResultToTsKvEntry(InfluxDataEntity influxDataEntity) {
        String key = influxDataEntity.key;
        long ts = influxDataEntity.time.toEpochMilli();
        return new BasicTsKvEntry(ts, toKvEntry(influxDataEntity, key));
    }

    protected TsKvEntry convertResultToTsKvEntry(InfluxDataLatestEntity influxDataLatestEntity) {
        String key = influxDataLatestEntity.key;
        long ts = influxDataLatestEntity.ts;
        return new BasicTsKvEntry(ts, toKvEntry(influxDataLatestEntity, key));
    }


    protected InfluxDataEntity toInfluxDataEntity(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry) {
        InfluxDataEntity influxDataEntity = new InfluxDataEntity();
        influxDataEntity.entityType = entityId.getEntityType().name();
        influxDataEntity.entityId = entityId.getId().toString();
        influxDataEntity.key = tsKvEntry.getKey();
        influxDataEntity.booleanValue = tsKvEntry.getBooleanValue().orElse(null);
        influxDataEntity.longValue = tsKvEntry.getLongValue().orElse(null);
        influxDataEntity.doubleValue = tsKvEntry.getDoubleValue().orElse(null);
        influxDataEntity.strValue = tsKvEntry.getStrValue().orElse(null);
        influxDataEntity.jsonValue = tsKvEntry.getJsonValue().orElse(null);
        influxDataEntity.time = Instant.ofEpochMilli(tsKvEntry.getTs());
        return influxDataEntity;
    }

    protected InfluxDataLatestEntity toInfluxDataLatestEntity(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry) {
        InfluxDataLatestEntity influxDataLatestEntity = new InfluxDataLatestEntity();
        influxDataLatestEntity.entityType = entityId.getEntityType().name();
        influxDataLatestEntity.entityId = entityId.getId().toString();
        influxDataLatestEntity.key = tsKvEntry.getKey();
        influxDataLatestEntity.booleanValue = tsKvEntry.getBooleanValue().orElse(null);
        influxDataLatestEntity.longValue = tsKvEntry.getLongValue().orElse(null);
        influxDataLatestEntity.doubleValue = tsKvEntry.getDoubleValue().orElse(null);
        influxDataLatestEntity.strValue = tsKvEntry.getStrValue().orElse(null);
        influxDataLatestEntity.jsonValue = tsKvEntry.getJsonValue().orElse(null);
        influxDataLatestEntity.ts = tsKvEntry.getTs();
        return influxDataLatestEntity;
    }

    protected KvEntry toKvEntry(InfluxDataEntity influxDataEntity, String key) {
        KvEntry kvEntry = null;
        if (influxDataEntity.strValue != null) {
            return new StringDataEntry(key, influxDataEntity.strValue);
        } else if (influxDataEntity.longValue != null) {
            return new LongDataEntry(key, influxDataEntity.longValue);
        } else if (influxDataEntity.doubleValue != null) {
            return new DoubleDataEntry(key, influxDataEntity.doubleValue);
        } else if (influxDataEntity.booleanValue != null) {
            return new BooleanDataEntry(key, influxDataEntity.booleanValue);
        } else {
            if (StringUtils.isNoneEmpty(influxDataEntity.jsonValue)) {
                kvEntry = new JsonDataEntry(key, influxDataEntity.jsonValue);
            } else {
                log.warn("All values in influxDataEntity [{}] are nullable ", influxDataEntity);
            }
        }
        return kvEntry;
    }

    protected KvEntry toKvEntry(InfluxDataLatestEntity influxDataLatestEntity, String key) {
        KvEntry kvEntry = null;
        if (influxDataLatestEntity.strValue != null) {
            return new StringDataEntry(key, influxDataLatestEntity.strValue);
        } else if (influxDataLatestEntity.longValue != null) {
            return new LongDataEntry(key, influxDataLatestEntity.longValue);
        } else if (influxDataLatestEntity.doubleValue != null) {
            return new DoubleDataEntry(key, influxDataLatestEntity.doubleValue);
        } else if (influxDataLatestEntity.booleanValue != null) {
            return new BooleanDataEntry(key, influxDataLatestEntity.booleanValue);
        } else {
            if (StringUtils.isNoneEmpty(influxDataLatestEntity.jsonValue)) {
                kvEntry = new JsonDataEntry(key, influxDataLatestEntity.jsonValue);
            } else {
                log.warn("All values in key-value row are nullable ");
            }
        }
        return kvEntry;
    }
}
