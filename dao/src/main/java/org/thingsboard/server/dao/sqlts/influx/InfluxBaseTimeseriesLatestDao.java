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
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.influxdb.client.DeleteApi;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.BasicTsKvEntry;
import org.thingsboard.server.common.data.kv.DeleteTsKvQuery;
import org.thingsboard.server.common.data.kv.StringDataEntry;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.kv.TsKvLatestRemovingResult;
import org.thingsboard.server.dao.timeseries.TimeseriesLatestDao;
import org.thingsboard.server.dao.util.InfluxDBTsDao;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.thingsboard.server.dao.model.ModelConstants.INFLUX_ENTITY_ID_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.INFLUX_ENTITY_TYPE_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.INFLUX_KEY_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.INFLUX_TS_KV_LATEST_MEASUREMENT;


/**
 * todo can optimize with async write and read
 */
@Component
@Slf4j
@InfluxDBTsDao
public class InfluxBaseTimeseriesLatestDao extends AbstractInfluxBaseTimeseriesDao implements TimeseriesLatestDao {


    private static final Instant START_INSTANT = Instant.EPOCH;

    private static final Instant END_INSTANT = Instant.now();

    @Override
    public ListenableFuture<TsKvEntry> findLatest(TenantId tenantId, EntityId entityId, String key) {
        String queryStr = Flux.from(getBucketName())
                .range(START_INSTANT)
                .filter(Restrictions.and(Restrictions.measurement().equal(INFLUX_TS_KV_LATEST_MEASUREMENT),
                        Restrictions.tag(INFLUX_ENTITY_ID_COLUMN).equal(entityId.getId().toString()),
                        Restrictions.tag(INFLUX_ENTITY_TYPE_COLUMN).equal(entityId.getEntityType().name()),
                        Restrictions.tag(INFLUX_KEY_COLUMN).equal(key)))
                .pivot(new String[]{"_time"}, new String[]{"_field"}, "_value").toString();
        log.debug("findLatest [{}] [{}] influx query string: [{}]", tenantId, entityId, queryStr);

        return Futures.submit(() -> {
            QueryApi queryApi = influxDBClient.getQueryApi();
            List<InfluxDataLatestEntity> result = queryApi.query(queryStr, getOrgName(), InfluxDataLatestEntity.class);
            if (result != null && result.size() > 0) {
                InfluxDataLatestEntity influxDataLatestEntity = result.get(0);
                return convertResultToTsKvEntry(influxDataLatestEntity);
            }
            return new BasicTsKvEntry(System.currentTimeMillis(), new StringDataEntry(key, null));
        }, readResultsProcessingExecutor);
    }

    @Override
    public ListenableFuture<List<TsKvEntry>> findAllLatest(TenantId tenantId, EntityId entityId) {
        String queryStr = Flux.from(getBucketName())
                .range(START_INSTANT)
                .filter(Restrictions.and(Restrictions.measurement().equal(INFLUX_TS_KV_LATEST_MEASUREMENT),
                        Restrictions.tag(INFLUX_ENTITY_ID_COLUMN).equal(entityId.getId().toString()),
                        Restrictions.tag(INFLUX_ENTITY_TYPE_COLUMN).equal(entityId.getEntityType().name())))
                .pivot(new String[]{"_time"}, new String[]{"_field"}, "_value").toString();
        log.debug("findAllLatest [{}] [{}] influx query string: [{}]", tenantId, entityId, queryStr);

        return Futures.submit(() -> {
            QueryApi queryApi = influxDBClient.getQueryApi();
            List<InfluxDataLatestEntity> result = queryApi.query(queryStr, getOrgName(), InfluxDataLatestEntity.class);
            List<TsKvEntry> tsKvEntries = Lists.newArrayList();
            if (result != null && result.size() > 0) {
                for (InfluxDataLatestEntity influxDataLatestEntity : result) {
                    tsKvEntries.add(convertResultToTsKvEntry(influxDataLatestEntity));
                }
            }
            return tsKvEntries;
        }, readResultsProcessingExecutor);
    }

    @Override
    public ListenableFuture<Void> saveLatest(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry) {
        InfluxDataLatestEntity influxDataLatestEntity = toInfluxDataLatestEntity(tenantId, entityId, tsKvEntry);
        return Futures.submit(() -> {
            WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
            writeApiBlocking.writeMeasurement(getBucketName(), getOrgName(), WritePrecision.MS, influxDataLatestEntity);
        }, readResultsProcessingExecutor);
    }

    @Override
    public ListenableFuture<TsKvLatestRemovingResult> removeLatest(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query) {
        String predicate = "_measurement=\"" + INFLUX_TS_KV_LATEST_MEASUREMENT +
                "\" AND \"" + INFLUX_ENTITY_ID_COLUMN + "\"=\"" + entityId.getId().toString() + "\"" +
                "\" AND \"" + INFLUX_ENTITY_TYPE_COLUMN + "\"=\"" + entityId.getEntityType().name() + "\"" +
                "\" AND \"" + INFLUX_KEY_COLUMN + "\"=\"" + query.getKey() + "\"";
        ListenableFuture<Void> submit = Futures.submit(() -> {
            DeleteApi deleteApi = influxDBClient.getDeleteApi();
            deleteApi.delete(OffsetDateTime.ofInstant(START_INSTANT, ZoneOffset.UTC), OffsetDateTime.ofInstant(END_INSTANT, ZoneOffset.UTC),
                    predicate, getBucketName(), getOrgName());
        }, readResultsProcessingExecutor);
        return Futures.transform(submit, v -> new TsKvLatestRemovingResult(query.getKey(), true),
                readResultsProcessingExecutor);
    }

    @Override
    public List<String> findAllKeysByDeviceProfileId(TenantId tenantId, DeviceProfileId deviceProfileId) {
        return Collections.emptyList();
    }

    @Override
    public List<String> findAllKeysByEntityIds(TenantId tenantId, List<EntityId> entityIds) {
        return Collections.emptyList();
    }
}
