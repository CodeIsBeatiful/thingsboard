///
/// Copyright © 2016-2022 The Thingsboard Authors
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import {
  DateEntityTableColumn,
  EntityTableColumn,
  EntityTableConfig
} from '@home/models/entity/entities-table-config.models';
import {
  SchedulerJob,
  SchedulerJobInfo,
  SchedulerJobTypeTranslationMap
} from '@app/shared/models/scheduler-job.models';
import { EntityType, entityTypeResources, entityTypeTranslations } from '@shared/models/entity-type.models';
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { SchedulerJobService } from '@core/http/scheduler-job.service';
import { PageLink } from '@shared/models/page/page-link';
import { SchedulerJobComponent } from '@home/pages/scheduler-job/scheduler-job.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';

@Injectable()
export class SchedulerJobTableConfigResolve implements Resolve<EntityTableConfig<SchedulerJob, PageLink, SchedulerJobInfo>> {

  private readonly config: EntityTableConfig<SchedulerJob, PageLink, SchedulerJobInfo> =
    new EntityTableConfig<SchedulerJob, PageLink, SchedulerJobInfo>();

  constructor(private translate: TranslateService,
              private datePipe: DatePipe,
              private store: Store<AppState>,
              private schedulerJobService: SchedulerJobService) {

    this.config.entityType = EntityType.SCHEDULER_JOB;
    this.config.entityComponent = SchedulerJobComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.SCHEDULER_JOB);
    this.config.entityResources = entityTypeResources.get(EntityType.SCHEDULER_JOB);

    this.config.addDialogStyle = {width: '800px'};

    this.config.columns.push(
      new DateEntityTableColumn<SchedulerJobInfo>('createdTime', 'common.created-time', this.datePipe, '150px'),
      new EntityTableColumn<SchedulerJobInfo>('name', 'scheduler-job.name', '30%'),
      new EntityTableColumn<SchedulerJobInfo>('type', 'scheduler-job.type', '70%', entity => {
        return this.translate.instant(SchedulerJobTypeTranslationMap.get(entity.type));
      })
    );

    this.config.deleteEntityTitle = schedulerJob => this.translate.instant('scheduler-job.delete-scheduler-job-title',
      { title: schedulerJob.name });
    this.config.deleteEntityContent = () => this.translate.instant('scheduler-job.delete-scheduler-job-text');
    this.config.deleteEntitiesTitle = count => this.translate.instant('scheduler-job.delete-scheduler-jobs-title', {count});
    this.config.deleteEntitiesContent = () => this.translate.instant('scheduler-job.delete-scheduler-jobs-text');

    this.config.entitiesFetchFunction = pageLink => this.schedulerJobService.getTenantSchedulerJobs(pageLink);
    this.config.loadEntity = id => this.schedulerJobService.getSchedulerJob(id.id);
    this.config.saveEntity = schedulerJob => this.schedulerJobService.saveSchedulerJob(schedulerJob);
    this.config.deleteEntity = id => this.schedulerJobService.deleteSchedulerJob(id.id);
  }

  resolve(): EntityTableConfig<SchedulerJob, PageLink, SchedulerJobInfo> {
    this.config.tableTitle = this.translate.instant('scheduler-job.scheduler-jobs');
    return this.config;
  }


}
