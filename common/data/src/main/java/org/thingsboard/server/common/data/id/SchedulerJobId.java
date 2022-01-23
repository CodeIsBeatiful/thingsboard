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


