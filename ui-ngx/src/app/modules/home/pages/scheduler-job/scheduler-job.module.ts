import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { HomeComponentsModule } from '@home/components/home-components.module';
import { SchedulerJobRoutingModule } from '@home/pages/scheduler-job/scheduler-job-routing.module';
import { SchedulerJobComponent } from '@home/pages/scheduler-job/scheduler-job.component';
import {SchedulerJobOtaUpdateComponent} from '@home/pages/scheduler-job/scheduler-job-ota-update.component';
import {SchedulerJobScheduleComponent} from '@home/pages/scheduler-job/scheduler-job-schedule.component';
import {TargetSelectComponent} from '@home/pages/scheduler-job/target-select.component';
import {SchedulerJobConfigurationComponent} from '@home/pages/scheduler-job/scheduler-job-configuration.component';
import {SchedulerJobAttributeUpdateComponent} from '@home/pages/scheduler-job/scheduler-job-attribute-update.component';
import {AttributeKeyValueTableComponent} from '@home/pages/scheduler-job/attribute-key-value-table.component';

@NgModule({
  declarations: [
    SchedulerJobComponent,
    SchedulerJobScheduleComponent,
    SchedulerJobConfigurationComponent,
    SchedulerJobOtaUpdateComponent,
    SchedulerJobAttributeUpdateComponent,
    AttributeKeyValueTableComponent,
    TargetSelectComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    SchedulerJobRoutingModule
  ]
})
export class SchedulerJobModule { }
