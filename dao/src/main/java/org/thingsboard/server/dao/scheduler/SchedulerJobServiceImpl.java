/**
 * Copyright © 2016-2021 The Thingsboard Authors
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
package org.thingsboard.server.dao.scheduler;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.scheduler.SchedulerJob;
import org.thingsboard.server.common.data.scheduler.SchedulerJobInfo;
import org.thingsboard.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.edge.EdgeDao;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.PaginatedRemover;
import org.thingsboard.server.dao.service.Validator;
import org.thingsboard.server.dao.tenant.TbTenantProfileCache;
import org.thingsboard.server.dao.tenant.TenantDao;

import static org.thingsboard.server.dao.service.Validator.validateId;

@Service
@Slf4j
public class SchedulerJobServiceImpl extends AbstractEntityService implements SchedulerJobService {

    public static final String INCORRECT_SCHEDULER_JOB_ID = "Incorrect schedulerJobId ";
    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    @Autowired
    private SchedulerJobDao schedulerJobDao;

    @Autowired
    private SchedulerJobInfoDao schedulerJobInfoDao;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private CustomerDao customerDao;
    
    @Autowired
    private EdgeDao edgeDao;

    @Autowired
    @Lazy
    private TbTenantProfileCache tenantProfileCache;

    @Override
    public SchedulerJob findSchedulerJobById(TenantId tenantId, SchedulerJobId schedulerJobId) {
        log.trace("Executing findSchedulerJobById [{}]", schedulerJobId);
        Validator.validateId(schedulerJobId, INCORRECT_SCHEDULER_JOB_ID + schedulerJobId);
        return schedulerJobDao.findById(tenantId, schedulerJobId.getId());
    }

    @Override
    public ListenableFuture<SchedulerJob> findSchedulerJobByIdAsync(TenantId tenantId, SchedulerJobId schedulerJobId) {
        log.trace("Executing findSchedulerJobByIdAsync [{}]", schedulerJobId);
        validateId(schedulerJobId, INCORRECT_SCHEDULER_JOB_ID + schedulerJobId);
        return schedulerJobDao.findByIdAsync(tenantId, schedulerJobId.getId());
    }

    @Override
    public SchedulerJobInfo findSchedulerJobInfoById(TenantId tenantId, SchedulerJobId schedulerJobId) {
        log.trace("Executing findSchedulerJobInfoById [{}]", schedulerJobId);
        Validator.validateId(schedulerJobId, INCORRECT_SCHEDULER_JOB_ID + schedulerJobId);
        return schedulerJobInfoDao.findById(tenantId, schedulerJobId.getId());
    }

    @Override
    public ListenableFuture<SchedulerJobInfo> findSchedulerJobInfoByIdAsync(TenantId tenantId, SchedulerJobId schedulerJobId) {
        log.trace("Executing findSchedulerJobInfoByIdAsync [{}]", schedulerJobId);
        validateId(schedulerJobId, INCORRECT_SCHEDULER_JOB_ID + schedulerJobId);
        return schedulerJobInfoDao.findByIdAsync(tenantId, schedulerJobId.getId());
    }

    @Override
    public SchedulerJob saveSchedulerJob(SchedulerJob schedulerJob) {
        log.trace("Executing saveSchedulerJob [{}]", schedulerJob);
        schedulerJobValidator.validate(schedulerJob, SchedulerJob::getTenantId);
        return schedulerJobDao.save(schedulerJob.getTenantId(), schedulerJob);
    }

    @Override
    public void deleteSchedulerJob(TenantId tenantId, SchedulerJobId schedulerJobId) {
        log.trace("Executing deleteSchedulerJob [{}]", schedulerJobId);
        Validator.validateId(schedulerJobId, INCORRECT_SCHEDULER_JOB_ID + schedulerJobId);
        deleteEntityRelations(tenantId, schedulerJobId);
        schedulerJobDao.removeById(tenantId, schedulerJobId.getId());
    }

    @Override
    public PageData<SchedulerJobInfo> findSchedulerJobsByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findSchedulerJobsByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        Validator.validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        Validator.validatePageLink(pageLink);
        return schedulerJobInfoDao.findSchedulerJobsByTenantId(tenantId.getId(), pageLink);
    }

    @Override
    public void deleteSchedulerJobsByTenantId(TenantId tenantId) {
        log.trace("Executing deleteSchedulerJobsByTenantId, tenantId [{}]", tenantId);
        Validator.validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        tenantSchedulerJobsRemover.removeEntities(tenantId, tenantId);
    }


    //todo 问题
    //1 删除部分无用方法 ok
    //2 profileConfiguration 配置

    private DataValidator<SchedulerJob> schedulerJobValidator =
            new DataValidator<SchedulerJob>() {
                @Override
                protected void validateCreate(TenantId tenantId, SchedulerJob data) {
                    DefaultTenantProfileConfiguration profileConfiguration =
                            (DefaultTenantProfileConfiguration)tenantProfileCache.get(tenantId).getProfileData().getConfiguration();
                    long maxSchedulerJobs = profileConfiguration.getMaxSchedulerJobs();
                    validateNumberOfEntitiesPerTenant(tenantId, schedulerJobDao, maxSchedulerJobs, EntityType.SCHEDULER_JOB);
                }

                @Override
                protected void validateDataImpl(TenantId tenantId, SchedulerJob schedulerJob) {
                    if (StringUtils.isEmpty(schedulerJob.getName())) {
                        throw new DataValidationException("SchedulerJob name should be specified!");
                    }
                    if (schedulerJob.getTenantId() == null) {
                        throw new DataValidationException("SchedulerJob should be assigned to tenant!");
                    } else {
                        Tenant tenant = tenantDao.findById(tenantId, schedulerJob.getTenantId().getId());
                        if (tenant == null) {
                            throw new DataValidationException("SchedulerJob is referencing to non-existent tenant!");
                        }
                    }
                }
            };

    private PaginatedRemover<TenantId, SchedulerJobInfo> tenantSchedulerJobsRemover =
            new PaginatedRemover<TenantId, SchedulerJobInfo>() {

        @Override
        protected PageData<SchedulerJobInfo> findEntities(TenantId tenantId, TenantId id, PageLink pageLink) {
            return schedulerJobInfoDao.findSchedulerJobsByTenantId(id.getId(), pageLink);
        }

        @Override
        protected void removeEntity(TenantId tenantId, SchedulerJobInfo entity) {
            deleteSchedulerJob(tenantId, new SchedulerJobId(entity.getUuidId()));
        }
    };

}
