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
package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;

import java.util.UUID;

@ApiModel
public class SchedulerJobId extends UUIDBased implements EntityId{

    @JsonCreator
    public SchedulerJobId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static SchedulerJobId fromString(String schedulerJobId) {
        return new SchedulerJobId(UUID.fromString(schedulerJobId));
    }


    @ApiModelProperty(position = 2, required = true, value = "string", example = "SCHEDULER_JOB", allowableValues = "SCHEDULER_JOB")
    @Override
    public EntityType getEntityType() {
        return EntityType.SCHEDULER_JOB;
    }

}


