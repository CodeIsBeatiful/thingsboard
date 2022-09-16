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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.influxdb.client.DeleteApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.Aggregation;
import org.thingsboard.server.common.data.kv.DeleteTsKvQuery;
import org.thingsboard.server.common.data.kv.ReadTsKvQuery;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.dao.sqlts.AggregationTimeseriesDao;
import org.thingsboard.server.dao.timeseries.TimeseriesDao;
import org.thingsboard.server.dao.util.InfluxDBTsDao;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static org.thingsboard.server.dao.model.ModelConstants.DOUBLE_VALUE_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.INFLUX_ENTITY_ID_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.INFLUX_ENTITY_TYPE_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.INFLUX_KEY_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.INFLUX_TS_KV_MEASUREMENT;
import static org.thingsboard.server.dao.model.ModelConstants.LONG_VALUE_COLUMN;

/**
 * todo can optimize with async write and read
 */
@Component
@Slf4j
@InfluxDBTsDao
public class InfluxBaseTimeseriesDao extends AbstractInfluxBaseTimeseriesDao implements AggregationTimeseriesDao, TimeseriesDao {

    @Override
    public ListenableFuture<List<TsKvEntry>> findAllAsync(TenantId tenantId, EntityId entityId, List<ReadTsKvQuery> queries) {
        List<ListenableFuture<List<TsKvEntry>>> futures = queries.stream().map(query -> findAllAsync(tenantId, entityId, query)).collect(Collectors.toList());
        return Futures.transform(Futures.allAsList(futures), new Function<>() {
            @Nullable
            @Override
            public List<TsKvEntry> apply(@Nullable List<List<TsKvEntry>> results) {
                if (results == null || results.isEmpty()) {
                    return null;
                }
                return results.stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
            }
        }, readResultsProcessingExecutor);
    }

    @Override
    public ListenableFuture<List<TsKvEntry>> findAllAsync(TenantId tenantId, EntityId entityId, ReadTsKvQuery query) {
        if (query.getAggregation() == Aggregation.NONE) {
            return findAllAsyncWithLimit(tenantId, entityId, query);
        } else {
            return findAndAggregateAsync(tenantId, entityId, query);
        }
    }

    @Override
    public ListenableFuture<Integer> save(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry, long ttl) {
        InfluxDataEntity influxDataEntity = toInfluxDataEntity(tenantId, entityId, tsKvEntry);
        ListenableFuture<Void> future = Futures.submit(() -> {
            WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
            writeApiBlocking.writeMeasurement(getBucketName(), getOrgName(), WritePrecision.MS, influxDataEntity);
        }, readResultsProcessingExecutor);
        return Futures.transform(future, v -> 1, readResultsProcessingExecutor);
    }

    @Override
    public ListenableFuture<Integer> savePartition(TenantId tenantId, EntityId entityId, long tsKvEntryTs, String key) {
        return Futures.immediateFuture(0);
    }

    @Override
    public ListenableFuture<Void> remove(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query) {
        String predicate = "_measurement=\"" + INFLUX_TS_KV_MEASUREMENT +
                "\" AND \"" + INFLUX_ENTITY_TYPE_COLUMN + "\"=\"" + entityId.getEntityType().name() + "\"" +
                "\" AND \"" + INFLUX_ENTITY_ID_COLUMN + "\"=\"" + entityId.getId().toString() + "\"" +
                "\" AND \"" + INFLUX_KEY_COLUMN + "\"=\"" + query.getKey() + "\"";
        return Futures.submit(() -> {
            DeleteApi deleteApi = influxDBClient.getDeleteApi();
            deleteApi.delete(OffsetDateTime.ofInstant(Instant.ofEpochMilli(query.getStartTs()), ZoneOffset.UTC),
                    OffsetDateTime.ofInstant(Instant.ofEpochMilli(query.getEndTs()), ZoneOffset.UTC),
                    predicate, getBucketName(), getOrgName());
        }, readResultsProcessingExecutor);
    }

    @Override
    public ListenableFuture<Void> removePartition(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query) {
        return Futures.immediateVoidFuture();
    }

    @Override
    public void cleanup(long systemTtl) {
        String predicate = "_measurement=\"" + INFLUX_TS_KV_MEASUREMENT;
        DeleteApi deleteApi = influxDBClient.getDeleteApi();
        deleteApi.delete(OffsetDateTime.now().minusSeconds(systemTtl), OffsetDateTime.now(), predicate, getBucketName(), getOrgName());
    }

    private ListenableFuture<List<TsKvEntry>> findAllAsyncWithLimit(TenantId tenantId, EntityId entityId, ReadTsKvQuery query) {
        String queryStr = Flux.from(getBucketName())
                .range(Instant.ofEpochMilli(query.getStartTs()), Instant.ofEpochMilli(query.getEndTs()))
                .filter(Restrictions.and(Restrictions.measurement().equal(INFLUX_TS_KV_MEASUREMENT),
                        Restrictions.tag(INFLUX_ENTITY_ID_COLUMN).equal(entityId.getId().toString()),
                        Restrictions.tag(INFLUX_ENTITY_TYPE_COLUMN).equal(entityId.getEntityType().name()),
                        Restrictions.tag(INFLUX_KEY_COLUMN).equal(query.getKey())))
                .pivot(new String[]{"_time"}, new String[]{"_field"}, "_value")
                .sort(new String[]{"_time"}, query.getOrder().equals(DESC_ORDER) ? true : false)
                .limit(query.getLimit()).toString();
        log.debug("findAllAsyncWithLimit [{}] [{}] influx query string: [{}]", tenantId, entityId, queryStr);

        return Futures.submit(() -> {
            List<TsKvEntry> tsKvEntries = Lists.newArrayList();
            List<InfluxDataEntity> influxDataEntities = influxDBClient.getQueryApi().query(queryStr, getOrgName(), InfluxDataEntity.class);
            for (InfluxDataEntity influxDataEntity : influxDataEntities) {
                tsKvEntries.add(convertResultToTsKvEntry(influxDataEntity));
            }
            return tsKvEntries;
        }, readResultsProcessingExecutor);
    }

    private ListenableFuture<List<TsKvEntry>> findAndAggregateAsync(TenantId tenantId, EntityId entityId, ReadTsKvQuery query) {
        final Aggregation aggregation = query.getAggregation();
        String fnName = aggregation.name().toLowerCase();
        // influx agg function mean = avg
        if (fnName.equals("avg")) {
            fnName = "mean";
        }
        Restrictions restrictions = Restrictions.and(Restrictions.measurement().equal(INFLUX_TS_KV_MEASUREMENT),
                Restrictions.tag(INFLUX_ENTITY_ID_COLUMN).equal(entityId.getId().toString()),
                Restrictions.tag(INFLUX_ENTITY_TYPE_COLUMN).equal(entityId.getEntityType().name()),
                Restrictions.tag(INFLUX_KEY_COLUMN).equal(query.getKey()),
                Restrictions.or(Restrictions.field().equal(DOUBLE_VALUE_COLUMN), Restrictions.field().equal(LONG_VALUE_COLUMN)));
        String queryStr = Flux.from(getBucketName())
                .range(Instant.ofEpochMilli(query.getStartTs()), Instant.ofEpochMilli(query.getEndTs()))
                .filter(restrictions)
                .aggregateWindow(query.getInterval(), ChronoUnit.MILLIS, fnName)
                .withCreateEmpty(false)
                .toFloat()
                .pivot(new String[]{"_time"}, new String[]{"_field"}, "_value")
                .sort(new String[]{"_time"}, query.getOrder().equals(DESC_ORDER) ? true : false)
                .limit(query.getLimit()).toString();
        log.debug("findAndAggregateAsync [{}] [{}] influx query string: [{}]", tenantId, entityId, queryStr);

        return Futures.submit(() -> {
            List<TsKvEntry> tsKvEntries = Lists.newArrayList();
            List<InfluxDataEntity> influxDataEntities = influxDBClient.getQueryApi().query(queryStr, getOrgName(), InfluxDataEntity.class);
            for (InfluxDataEntity influxDataEntity : influxDataEntities) {
                tsKvEntries.add(convertResultToTsKvEntry(influxDataEntity));
            }
            return tsKvEntries;
        }, readResultsProcessingExecutor);
    }

}
