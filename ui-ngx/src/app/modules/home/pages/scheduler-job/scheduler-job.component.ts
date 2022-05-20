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

import {ChangeDetectorRef, Component, Inject} from '@angular/core';
import {Store} from '@ngrx/store';
import {AppState} from '@core/core.state';
import {TranslateService} from '@ngx-translate/core';
import {EntityTableConfig} from '@home/models/entity/entities-table-config.models';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EntityComponent} from '@home/components/entity/entity.component';
import {
  MsgTypeMap,
  SchedulerJob, SchedulerJobTypes, SchedulerJobTypeTranslationMap
} from '@app/shared/models/scheduler-job.models';
import {ActionNotificationShow} from '@core/notification/notification.actions';
import {Subscription} from 'rxjs';

@Component({
  selector: 'tb-scheduler-job',
  templateUrl: './scheduler-job.component.html'
})
export class SchedulerJobComponent extends EntityComponent<SchedulerJob> {

  selectedTab = 0;

  jobTypes = SchedulerJobTypes;

  msgTypes = MsgTypeMap;

  schedulerJobTypeTranslationMap = SchedulerJobTypeTranslationMap;

  constructor(protected store: Store<AppState>,
              protected translate: TranslateService,
              @Inject('entity') protected entityValue: SchedulerJob,
              @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<SchedulerJob>,
              public fb: FormBuilder,
              protected cd: ChangeDetectorRef) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  hideDelete() {
    if (this.entitiesTableConfig) {
      return !this.entitiesTableConfig.deleteEnabled(this.entity);
    } else {
      return false;
    }
  }

  buildForm(entity: SchedulerJob): FormGroup {
    const formGroup = this.fb.group({
      name: [entity ? entity.name : '', [Validators.required, Validators.maxLength(255)]],
      configuration: [entity ? entity.configuration : null, [Validators.required]],
      schedule: [entity ? entity.schedule : null, [Validators.required]],
      type: [entity ? entity.type : null, [Validators.required]],
      additionalInfo: this.fb.group(
        {
          description: [entity && entity.additionalInfo ? entity.additionalInfo.description : ''],
        }
      )
    });

    formGroup.get('type').valueChanges.subscribe((type) => {
      this.entityForm.patchValue({
        configuration: null
      });
    });
    return formGroup;
  }

  updateForm(value: SchedulerJob) {
    this.entityForm.patchValue({
      name: value.name,
      schedule: value.schedule,
      configuration: value.configuration,
      type: value.type,
      additionalInfo: {
        description: value.additionalInfo ? value.additionalInfo.description : ''
      }
    }, {emitEvent: false});
  }

  onSchedulerJobIdCopied() {
    this.store.dispatch(new ActionNotificationShow(
      {
        message: this.translate.instant('scheduler-job.idCopiedMessage'),
        type: 'success',
        duration: 750,
        verticalPosition: 'bottom',
        horizontalPosition: 'right'
      }));
  }

}
