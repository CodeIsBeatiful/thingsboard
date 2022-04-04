import { EntityId } from './entity-id';
import { EntityType } from '@shared/models/entity-type.models';

export class SchedulerJobId implements EntityId {
  entityType = EntityType.SCHEDULER_JOB;
  id: string;
  constructor(id: string) {
    this.id = id;
  }
}
