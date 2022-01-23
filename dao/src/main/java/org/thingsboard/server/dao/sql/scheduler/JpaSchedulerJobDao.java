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
package org.thingsboard.server.dao.sql.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.scheduler.SchedulerJob;
import org.thingsboard.server.dao.dashboard.DashboardDao;
import org.thingsboard.server.dao.model.sql.SchedulerJobEntity;
import org.thingsboard.server.dao.scheduler.SchedulerJobDao;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;
import org.thingsboard.server.dao.sql.dashboard.DashboardRepository;

import java.util.UUID;

/**
 * Created by blackstar-baba on 1/10/2022.
 */
@Component
public class JpaSchedulerJobDao extends JpaAbstractSearchTextDao<SchedulerJobEntity, SchedulerJob> implements SchedulerJobDao {

    @Autowired
    SchedulerJobRepository schedulerJobRepository;

    @Override
    protected Class<SchedulerJobEntity> getEntityClass() {
        return SchedulerJobEntity.class;
    }

    @Override
    protected CrudRepository<SchedulerJobEntity, UUID> getCrudRepository() {
        return schedulerJobRepository;
    }

    @Override
    public Long countByTenantId(TenantId tenantId) {
        return schedulerJobRepository.countByTenantId(tenantId.getId());
    }
}
