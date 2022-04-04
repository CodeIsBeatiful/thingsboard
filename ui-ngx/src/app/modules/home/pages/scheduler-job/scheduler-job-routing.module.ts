import { RouterModule, Routes } from '@angular/router';
import { EntitiesTableComponent } from '@home/components/entity/entities-table.component';
import { Authority } from '@shared/models/authority.enum';
import { NgModule } from '@angular/core';
import { SchedulerJobTableConfigResolve } from '@home/pages/scheduler-job/scheduler-job-table-config.resolve';

const routes: Routes = [
  {
    path: 'schedulerJobs',
    component: EntitiesTableComponent,
    data: {
      auth: [Authority.TENANT_ADMIN],
      title: 'scheduler-job.scheduler-jobs',
      breadcrumb: {
        label: 'scheduler-job.scheduler-jobs',
        icon: 'schedule'
      }
    },
    resolve: {
      entitiesTableConfig: SchedulerJobTableConfigResolve
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    SchedulerJobTableConfigResolve
  ]
})
export class SchedulerJobRoutingModule { }
