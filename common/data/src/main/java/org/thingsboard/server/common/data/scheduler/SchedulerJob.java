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
package org.thingsboard.server.common.data.scheduler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Arrays;
import org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo;
import org.thingsboard.server.common.data.id.SchedulerJobId;

public class SchedulerJob extends SchedulerJobInfo{

    private transient JsonNode configuration;

    @JsonIgnore
    private byte[] configurationBytes;

    @JsonIgnore
    public void setConfigurationBytes(byte[] configurationBytes) {
        this.configurationBytes = configurationBytes;
    }

    public String toString() {
        return "SchedulerJob(super=" + super.toString() + ", configuration=" + getConfiguration() + ", configurationBytes=" + Arrays.toString(getConfigurationBytes()) + ")";
    }

    public byte[] getConfigurationBytes() {
        return this.configurationBytes;
    }

    public SchedulerJob() {}

    public SchedulerJob(SchedulerJobId id) {
        super(id);
    }

    public SchedulerJob(SchedulerJob schedulerJob) {
        super(schedulerJob);
        setConfiguration(schedulerJob.getConfiguration());
    }

    public JsonNode getConfiguration() {
        return SearchTextBasedWithAdditionalInfo.getJson(() -> this.configuration, () -> this.configurationBytes);
    }

    public void setConfiguration(JsonNode data) {
        setJson(data, json -> this.configuration = json, bytes -> this.configurationBytes = bytes);
    }
}
