package org.thingsboard.server.common.data.scheduler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.SchedulerJobId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.Arrays;

public class SchedulerJobInfo extends SearchTextBasedWithAdditionalInfo<SchedulerJobId> implements HasName, HasTenantId, HasCustomerId {

    private TenantId tenantId;

    private CustomerId customerId;

    @NoXss
    private String name;

    @NoXss
    private String type;

    private transient JsonNode schedule;

    @JsonIgnore
    private byte[] scheduleBytes;

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public void setScheduleBytes(byte[] schedulerBytes) {
        this.scheduleBytes = schedulerBytes;
    }

    public String toString() {
        return "SchedulerJobInfo(super=" + super.toString() + ", tenantId=" + getTenantId() + ", customerId=" + getCustomerId() + ", name=" + getName() + ", type=" + getType() + ", schedule=" + getSchedule() + ", scheduleBytes=" + Arrays.toString(getScheduleBytes()) + ")";
    }


    public TenantId getTenantId() {
        return this.tenantId;
    }

    public CustomerId getCustomerId() {
        return this.customerId;
    }

    public String getType() {
        return this.type;
    }

    public byte[] getScheduleBytes() {
        return this.scheduleBytes;
    }

    public SchedulerJobInfo() {}

    public SchedulerJobInfo(SchedulerJobId id) {
        super(id);
    }

    public SchedulerJobInfo(SchedulerJobInfo schedulerJobInfo) {
        super(schedulerJobInfo);
        this.tenantId = schedulerJobInfo.getTenantId();
        this.customerId = schedulerJobInfo.getCustomerId();
        this.name = schedulerJobInfo.getName();
        this.type = schedulerJobInfo.getType();
        setSchedule(schedulerJobInfo.getSchedule());
    }

    public String getSearchText() {
        return getName();
    }

    public String getName() {
        return this.name;
    }

    public JsonNode getSchedule() {
        return SearchTextBasedWithAdditionalInfo.getJson(() -> this.schedule, () -> this.scheduleBytes);
    }

    public void setSchedule(JsonNode data) {
        setJson(data, json -> this.schedule = json, bytes -> this.scheduleBytes = bytes);
    }

    @JsonIgnore
    public EntityType getEntityType() {
        return EntityType.SCHEDULER_JOB;
    }
}
