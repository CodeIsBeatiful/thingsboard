import {ChangeDetectorRef, Component, Inject, Input, OnDestroy, OnInit} from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { TranslateService } from '@ngx-translate/core';
import { EntityTableConfig } from '@home/models/entity/entities-table-config.models';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EntityComponent } from '@home/components/entity/entity.component';
import {
  SchedulerJob, SchedulerJobTypes, SchedulerJobTypeTranslationMap
} from '@app/shared/models/scheduler-job.models';
import { ActionNotificationShow } from '@core/notification/notification.actions';

@Component({
  selector: 'tb-scheduler-job',
  templateUrl: './scheduler-job.component.html'
})
export class SchedulerJobComponent extends EntityComponent<SchedulerJob>  {

  selectedTab = 0;

  jobTypes = SchedulerJobTypes;

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
    return this.fb.group({
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
    });
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
