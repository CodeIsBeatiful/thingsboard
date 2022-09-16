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

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

import static org.thingsboard.server.dao.model.ModelConstants.BOOLEAN_VALUE_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.DOUBLE_VALUE_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.INFLUX_ENTITY_ID_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.INFLUX_ENTITY_TYPE_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.INFLUX_KEY_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.INFLUX_TS_KV_MEASUREMENT;
import static org.thingsboard.server.dao.model.ModelConstants.JSON_VALUE_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.LONG_VALUE_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.STRING_VALUE_COLUMN;


@Measurement(name = INFLUX_TS_KV_MEASUREMENT)
public class InfluxDataEntity {

    @Column(name = INFLUX_ENTITY_TYPE_COLUMN, tag = true)
    String entityType;

    @Column(name = INFLUX_ENTITY_ID_COLUMN, tag = true)
    String entityId;

    @Column(name = INFLUX_KEY_COLUMN, tag = true)
    String key;

    @Column(name = BOOLEAN_VALUE_COLUMN)
    Boolean booleanValue;

    @Column(name = STRING_VALUE_COLUMN)
    String strValue;

    @Column(name = LONG_VALUE_COLUMN)
    Long longValue;

    @Column(name = DOUBLE_VALUE_COLUMN)
    Double doubleValue;

    @Column(name = JSON_VALUE_COLUMN)
    String jsonValue;

    @Column(timestamp = true)
    Instant time;

    @Override
    public String toString() {
        return "InfluxDataEntity{" +
                "entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", key='" + key + '\'' +
                ", booleanValue=" + booleanValue +
                ", strValue='" + strValue + '\'' +
                ", longValue=" + longValue +
                ", doubleValue=" + doubleValue +
                ", jsonValue='" + jsonValue + '\'' +
                ", time=" + time +
                '}';
    }
}
