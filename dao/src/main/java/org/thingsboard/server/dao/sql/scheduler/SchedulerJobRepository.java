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
package org.thingsboard.server.dao.sql.scheduler;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.model.sql.SchedulerJobEntity;
import org.thingsboard.server.dao.model.sql.SchedulerJobInfoEntity;

import java.util.UUID;

/**
 * Created by blackstar-baba on 1/10/2022.
 */
public interface SchedulerJobRepository extends CrudRepository<SchedulerJobEntity, UUID> {

    Long countByTenantId(UUID tenantId);

    @Query("SELECT di FROM SchedulerJobEntity di WHERE di.tenantId = :tenantId " +
            "AND LOWER(di.searchText) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<SchedulerJobEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                                @Param("searchText") String searchText,
                                                Pageable pageable);
}
