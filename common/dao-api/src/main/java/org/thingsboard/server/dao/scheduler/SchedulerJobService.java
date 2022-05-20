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
package org.thingsboard.server.dao.scheduler;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.id.SchedulerJobId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.scheduler.SchedulerJob;
import org.thingsboard.server.common.data.scheduler.SchedulerJobInfo;


public interface SchedulerJobService {

    SchedulerJob findSchedulerJobById(TenantId tenantId, SchedulerJobId schedulerJobId);

    ListenableFuture<SchedulerJob> findSchedulerJobByIdAsync(TenantId tenantId, SchedulerJobId schedulerJobId);

    SchedulerJobInfo findSchedulerJobInfoById(TenantId tenantId, SchedulerJobId schedulerJobId);

    ListenableFuture<SchedulerJobInfo> findSchedulerJobInfoByIdAsync(TenantId tenantId, SchedulerJobId schedulerJobId);

    SchedulerJob saveSchedulerJob(SchedulerJob schedulerJob);

    void deleteSchedulerJob(TenantId tenantId, SchedulerJobId schedulerJobId);

    PageData<SchedulerJobInfo> findSchedulerJobInfosByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<SchedulerJob> findSchedulerJobsByTenantId(TenantId tenantId, PageLink pageLink);

    void deleteSchedulerJobsByTenantId(TenantId tenantId);


}
