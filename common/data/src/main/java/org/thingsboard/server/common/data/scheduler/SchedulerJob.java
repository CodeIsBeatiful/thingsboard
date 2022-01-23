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
