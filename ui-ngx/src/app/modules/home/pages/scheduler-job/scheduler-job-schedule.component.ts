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

import {
  Component,
  forwardRef,
  Input, OnDestroy,
  OnInit
} from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor, FormArray,
  FormBuilder, FormControl,
  FormGroup, NG_VALUE_ACCESSOR, ValidationErrors, Validators
} from '@angular/forms';
import {Store} from '@ngrx/store';
import {AppState} from '@app/core/core.state';
import {TranslateService} from '@ngx-translate/core';
import {UtilsService} from '@core/services/utils.service';
import {DialogService} from '@core/services/dialog.service';
import {
  RepeatTypes, RepeatTypeTranslationMap, Schedule
} from '@app/shared/models/scheduler-job.models';
import {getDefaultTimezone, TimeUnit, timeUnitTranslationMap} from '@shared/models/time/time.models';
import {dayOfWeekTranslations} from '@shared/models/device.models';
import * as _moment from 'moment';
import {Subscription} from 'rxjs';

@Component({
  selector: 'tb-scheduler-job-schedule',
  templateUrl: './scheduler-job-schedule.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SchedulerJobScheduleComponent),
      multi: true
    }
  ]
})
export class SchedulerJobScheduleComponent implements OnInit, OnDestroy, ControlValueAccessor {

  private valueChange$: Subscription = null;
  private repeatTypeValueChange$: Subscription = null;

  constructor(protected store: Store<AppState>,
              private utils: UtilsService,
              private dialog: DialogService,
              private translate: TranslateService,
              private fb: FormBuilder) {
    this.repeatTypes = RepeatTypes;
    this.scheduleFormGroup = this.fb.group({
      timezone: [null, [Validators.required]],
      startTime: [null, [Validators.required]],
      repeat: [false, [Validators.required]]
    });
  }

  @Input()
  disabled: boolean;

  public scheduleFormGroup: FormGroup;

  public repeatFormGroup: FormGroup;

  public repeatTypes = RepeatTypes;

  public repeatTypeTranslationMap = RepeatTypeTranslationMap;

  public timeUnits = Object.values(TimeUnit).filter(item => item !== TimeUnit.DAYS);

  public timeUnitTranslations = timeUnitTranslationMap;

  public dayOfWeekTranslationsArray = dayOfWeekTranslations;

  public firstRowDays = Array(4).fill(0).map((x, i) => i);

  public secondRowDays = Array(3).fill(0).map((x, i) => i + 4);

  private modelValue: Schedule;

  private propagateChange = (v: any) => { };

  ngOnInit(): void {
    this.scheduleFormGroup.get('repeat').valueChanges.subscribe((repeatValue) => {
      this.destroyFormObservable();
      if (this.modelValue.repeat === null && repeatValue) {
        this.scheduleFormGroup.addControl('repeatType',
          this.fb.control(this.repeatTypes[0], [Validators.required]));
        this.scheduleFormGroup.addControl('endTime',
          this.fb.control(null, [Validators.required]));
        this.modelValue.repeat = {
          type: this.repeatTypes[0],
          endTime: null
        };
        this.updateRepeatTypeObservable();
      } else if (this.modelValue.repeat && !repeatValue) {
        this.scheduleFormGroup.removeControl('repeatType');
        this.scheduleFormGroup.removeControl('endTime');
        this.modelValue.repeat = null;
      }
      this.initFormObservable();
    });
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
    // send default
    if (!this.disabled) {
      this.propagateChange(this.modelValue);
    }
  }

  registerOnTouched(fn: any): void {
  }

  writeValue(value: Schedule): void {
    this.destroyFormObservable();
    this.scheduleFormGroup.removeControl('repeatType');
    this.scheduleFormGroup.removeControl('endTime');
    this.scheduleFormGroup.removeControl('timer');
    this.scheduleFormGroup.removeControl('weekly');
    if (value != null) {
      this.modelValue = value;
      if (value.repeat) {
        this.scheduleFormGroup.patchValue({
          timezone: value.timezone,
          startTime: this.utcTimestampToDate(value.startTime),
          repeat: true
        }, {emitEvent: false});
        this.scheduleFormGroup.addControl('repeatType',
          this.fb.control(value.repeat.type, [Validators.required]));
        this.scheduleFormGroup.addControl('endTime',
          this.fb.control(this.utcTimestampToDate(value.repeat.endTime), [Validators.required]));
        if (value.repeat.type === 'TIMER') {
          this.scheduleFormGroup.addControl('timer',
            this.fb.group({
              repeatInterval: [value.repeat.repeatInterval, [Validators.required, Validators.min(1)]],
              timeUnit: [value.repeat.timeUnit, [Validators.required]]
            }));
        } else if (value.repeat.type === 'WEEKLY') {
          const daysOfWeek = new Array(7).fill(false).map((item, index) => value.repeat.repeatOn.indexOf(index + 1) > -1);
          this.scheduleFormGroup.addControl('weekly',
            this.fb.array(daysOfWeek, this.validateDayOfWeeks));
        }
        // fix some select not disable
        this.setDisabledState(this.disabled);
        this.updateRepeatTypeObservable();
      } else {
        this.scheduleFormGroup.patchValue({
          timezone: value.timezone,
          startTime: this.utcTimestampToDate(value.startTime),
          repeat: false
        }, {emitEvent: false});
      }
    } else {
      const date = new Date();
      const timeZone = getDefaultTimezone();
      this.modelValue = {
        startTime: this.dateToUTCTimestamp(date),
        timezone: timeZone,
        repeat: null
      };
      this.scheduleFormGroup.patchValue({
        timezone: timeZone,
        startTime: date,
        repeat: false
      }, {emitEvent: false});
    }
    this.initFormObservable();
  }

  updateModel() {
    const value = this.scheduleFormGroup.value;
    if (this.modelValue) {
      this.modelValue.timezone = value.timezone;
      this.modelValue.startTime = this.dateToUTCTimestamp(value.startTime);
      if (value.repeat) {
        this.modelValue.repeat = {
          type: value.repeatType,
          endTime: this.dateToUTCTimestamp(value.endTime)
        };
        if (value.repeatType === 'TIMER') {
          this.modelValue.repeat.timeUnit = value.timer.timeUnit;
          this.modelValue.repeat.repeatInterval = value.timer.repeatInterval;
        } else if (value.repeatType === 'WEEKLY') {
          this.modelValue.repeat.repeatOn = value.weekly
            .map((day: boolean, index: number) => day ? index + 1 : null)
            .filter(day => !!day);
        }
      }
      if (!this.scheduleFormGroup.invalid) {
        this.propagateChange(this.modelValue);
      } else {
        this.propagateChange(null);
      }

    }
  }

  setDisabledState?(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.scheduleFormGroup.disable({emitEvent: false});
    } else {
      this.scheduleFormGroup.enable({emitEvent: false});
    }
  }

  ngOnDestroy(): void {
    if (this.valueChange$) {
      this.valueChange$.unsubscribe();
    }
  }

  weeklyRepeatControl(index: number): FormControl {
    return (this.scheduleFormGroup.get('weekly') as FormArray).at(index) as FormControl;
  }

  validateDayOfWeeks(control: AbstractControl): ValidationErrors | null {
    const dayOfWeeks: boolean[] = control.value;
    if (!dayOfWeeks || !dayOfWeeks.length || !dayOfWeeks.find(v => v === true)) {
      return {
        weekly: true
      };
    }
    return null;
  }

  validateItems(control: AbstractControl): ValidationErrors | null {
    const items: any[] = control.value;
    if (!items || !items.length || !items.find(v => v.enabled === true)) {
      return {
        weekly: true
      };
    }
    return null;
  }

  dateToUTCTimestamp(date: Date): number {
    if (typeof date === 'number' || date === null) {
      return 0;
    }
    return _moment.utc(date).valueOf();
  }

  utcTimestampToDate(time = 0): Date {
    return new Date(time + new Date(time).getTimezoneOffset() * 60 * 1000);
  }

  private updateRepeatTypeObservable() {
    if (this.repeatTypeValueChange$) {
      this.repeatTypeValueChange$.unsubscribe();
    }
    this.repeatTypeValueChange$ = this.scheduleFormGroup.get('repeatType').valueChanges.subscribe((repeatTypeValue) => {
      if (this.modelValue.repeat && this.modelValue.repeat.type !== repeatTypeValue) {
        this.destroyFormObservable();
        this.scheduleFormGroup.removeControl('timer');
        this.scheduleFormGroup.removeControl('weekly');
        if (repeatTypeValue === 'TIMER') {
          this.scheduleFormGroup.addControl('timer',
            this.fb.group({
              repeatInterval: [1, [Validators.required, Validators.min(1)]],
              timeUnit: [TimeUnit.SECONDS, [Validators.required]]
            }));
          this.modelValue.repeat = {
            endTime: this.modelValue.repeat.endTime,
            repeatInterval: 0,
            timeUnit: TimeUnit.SECONDS,
            type: this.modelValue.repeat.type
          };
          this.modelValue.repeat.repeatInterval = 0;
          this.modelValue.repeat.timeUnit = TimeUnit.SECONDS;
        } else if (repeatTypeValue === 'WEEKLY') {
          this.scheduleFormGroup.addControl('weekly',
            this.fb.array(new Array(7).fill(false), this.validateDayOfWeeks));
          this.modelValue.repeat = {
            endTime: this.modelValue.repeat.endTime,
            repeatOn: [],
            type: this.modelValue.repeat.type
          };
        } else {
          this.modelValue.repeat = {
            endTime: this.modelValue.repeat.endTime,
            type: this.modelValue.repeat.type
          };
        }
        this.initFormObservable();
      }
    });
  }

  private initFormObservable() {
    this.valueChange$ = this.scheduleFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });
  }

  private destroyFormObservable() {
    if (this.valueChange$) {
      this.valueChange$.unsubscribe();
    }
  }

}




