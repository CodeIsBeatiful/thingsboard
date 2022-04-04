import { BaseData } from '@shared/models/base-data';
import { SchedulerJobId } from './id/scheduer-job-id';
import { TenantId } from '@shared/models/id/tenant-id';
import { CustomerId } from '@shared/models/id/customer-id';
import { EntitySearchQuery } from '@shared/models/relation.models';
import {EntityId} from '@shared/models/id/entity-id';

export interface SchedulerJobInfo extends BaseData<SchedulerJobId> {
  tenantId?: TenantId;
  customerId?: CustomerId;
  name: string;
  type: string;
  additionalInfo?: any;
  schedule: Schedule;
}

export interface Schedule {
  startTime: number;
  timezone: string;
  repeat?: Repeat;
}

export interface Configuration {
  originatorId: EntityId;
  msgType: string;
  msgBody?: any;
  metadata?: any;
}

export interface Repeat {
  type: string;
  endTime: number;
  repeatInterval?: number;
  timeUnit?: string;
  repeatOn?: number[];
}


export interface SchedulerJob extends SchedulerJobInfo {
  configuration: string;
}

export interface SchedulerJobSearchQuery extends EntitySearchQuery {
  schedulerJobTypes: Array<string>;
}

export const RepeatTypes = ['DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY', 'TIMER'];

export const RepeatTypeTranslationMap = new Map<string, string>(
  [
    ['DAILY', 'scheduler-job.repeat-types.daily'],
    ['WEEKLY', 'scheduler-job.repeat-types.weekly'],
    ['MONTHLY', 'scheduler-job.repeat-types.monthly'],
    ['YEARLY', 'scheduler-job.repeat-types.yearly'],
    ['TIMER', 'scheduler-job.repeat-types.timer']
  ]
);

export const SchedulerJobTypes = ['updateAttributes', 'sendRpcRequestToDevice', 'updateFirmware', 'updateSoftware'];

export const MsgTypeMap = new Map<string, string>(
  [
    ['updateAttributes', 'POST_ATTRIBUTES_REQUEST'],
    ['sendRpcRequestToDevice', 'RPC_CALL_FROM_SERVER_TO_DEVICE'],
    ['updateFirmware', 'FIRMWARE_UPDATED'],
    ['updateSoftware', 'SOFTWARE_UPDATED'],
  ]
);


export const SchedulerJobTypeTranslationMap = new Map<string, string>(
  [
    ['updateAttributes', 'scheduler-job.types.update-attributes'],
    ['sendRpcRequestToDevice', 'scheduler-job.types.rpc-to-device'],
    ['updateFirmware', 'scheduler-job.types.update-firmware'],
    ['updateSoftware', 'scheduler-job.types.update-software'],
  ]
);

export const AttributeScopes = ['CLIENT_SCOPE', 'SERVER_SCOPE', 'SHARED_SCOPE'];

export const AttributeScopeTranslations = new Map<string, string>(
  [
    ['CLIENT_SCOPE', 'attribute.scope-client'],
    ['SERVER_SCOPE', 'attribute.scope-server'],
    ['SHARED_SCOPE', 'attribute.scope-shared']
  ]
);
