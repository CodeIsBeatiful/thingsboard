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
import org.thingsboard.server.common.data.scheduler.SchedulerJob;
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
public final class SchedulerJobEntity extends BaseSqlEntity<SchedulerJob> implements SearchTextEntity<SchedulerJob> {

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
    private JsonNode scheduler;

    @Type(type = "json")
    @Column(name = ModelConstants.SCHEDULER_JOB_CONFIGURATION_PROPERTY)
    private JsonNode configuration;


    @Type(type = "json")
    @Column(name = ModelConstants.SCHEDULER_JOB_ADDITIONAL_INFO_PROPERTY)
    private JsonNode additionalInfo;

    public SchedulerJobEntity() {
        super();
    }

    public SchedulerJobEntity(SchedulerJob schedulerJob) {
        if (schedulerJob.getId() != null) {
            this.setUuid(schedulerJob.getId().getId());
        }
        this.setCreatedTime(schedulerJob.getCreatedTime());
        if (schedulerJob.getTenantId() != null) {
            this.tenantId = schedulerJob.getTenantId().getId();
        }
        this.name = schedulerJob.getName();
        this.searchText = schedulerJob.getSearchText();
        if(schedulerJob.getCustomerId()!= null) {
            this.customerId = schedulerJob.getCustomerId().getId();
        }
        this.type = schedulerJob.getType();
        this.scheduler = schedulerJob.getSchedule();
        this.configuration = schedulerJob.getConfiguration();
        this.additionalInfo = schedulerJob.getAdditionalInfo();
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
    public SchedulerJob toData() {
        SchedulerJob schedulerJob = new SchedulerJob(new SchedulerJobId(this.getUuid()));
        schedulerJob.setCreatedTime(createdTime);
        if (tenantId != null) {
            schedulerJob.setTenantId(new TenantId(tenantId));
        }
        schedulerJob.setName(name);
        if(customerId != null) {
            schedulerJob.setCustomerId(new CustomerId(customerId));
        }
        schedulerJob.setType(type);
        schedulerJob.setSchedule(scheduler);
        schedulerJob.setConfiguration(configuration);
        schedulerJob.setAdditionalInfo(additionalInfo);
        return schedulerJob;
    }
}
