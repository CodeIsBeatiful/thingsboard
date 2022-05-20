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
package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.SchedulerJobId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.scheduler.SchedulerJobInfo;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.SearchTextEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.SCHEDULER_JOB_COLUMN_FAMILY_NAME)
public final class SchedulerJobInfoEntity extends BaseSqlEntity<SchedulerJobInfo> implements SearchTextEntity<SchedulerJobInfo> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Column(name = ModelConstants.SCHEDULER_JOB_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @Column(name = ModelConstants.SCHEDULER_JOB_NAME_PROPERTY)
    private String name;

    @Column(name = ModelConstants.SEARCH_TEXT_PROPERTY)
    private String searchText;

    @Column(name = ModelConstants.SCHEDULER_JOB_CUSTOMER_ID_PROPERTY, columnDefinition = "uuid")
    private UUID customerId;

    @Column(name = ModelConstants.SCHEDULER_JOB_TYPE_PROPERTY)
    private String type;

    @Type(type = "json")
    @Column(name = ModelConstants.SCHEDULER_JOB_SCHEDULE_PROPERTY)
    private JsonNode schedule;

    @Type(type = "json")
    @Column(name = ModelConstants.SCHEDULER_JOB_ADDITIONAL_INFO_PROPERTY)
    private JsonNode additionalInfo;

    public SchedulerJobInfoEntity() {
        super();
    }

    public SchedulerJobInfoEntity(SchedulerJobInfo schedulerJobInfo) {
        if (schedulerJobInfo.getId() != null) {
            this.setUuid(schedulerJobInfo.getId().getId());
        }
        this.setCreatedTime(schedulerJobInfo.getCreatedTime());
        if (schedulerJobInfo.getTenantId() != null) {
            this.tenantId = schedulerJobInfo.getTenantId().getId();
        }
        this.name = schedulerJobInfo.getName();
        this.searchText = schedulerJobInfo.getSearchText();
        if(schedulerJobInfo.getCustomerId()!= null) {
            this.customerId = schedulerJobInfo.getCustomerId().getId();
        }
        this.type = schedulerJobInfo.getType();
        this.schedule = schedulerJobInfo.getSchedule();
        this.additionalInfo = schedulerJobInfo.getAdditionalInfo();
    }

    @Override
    public String getSearchTextSource() {
        return name;
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public SchedulerJobInfo toData() {
        SchedulerJobInfo schedulerJobInfo = new SchedulerJobInfo(new SchedulerJobId(this.getUuid()));
        schedulerJobInfo.setCreatedTime(createdTime);
        if (tenantId != null) {
            schedulerJobInfo.setTenantId(new TenantId(tenantId));
        }
        schedulerJobInfo.setName(name);
        if(customerId != null) {
            schedulerJobInfo.setCustomerId(new CustomerId(customerId));
        }
        schedulerJobInfo.setType(type);
        schedulerJobInfo.setSchedule(schedule);
        schedulerJobInfo.setAdditionalInfo(additionalInfo);
        return schedulerJobInfo;
    }
}
