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

import {Component, forwardRef, Input, OnDestroy, OnInit} from '@angular/core';
import {ControlValueAccessor, FormBuilder, FormGroup, NG_VALUE_ACCESSOR} from '@angular/forms';
import {Store} from '@ngrx/store';
import {AppState} from '@app/core/core.state';
import {TranslateService} from '@ngx-translate/core';
import {UtilsService} from '@core/services/utils.service';
import {DialogService} from '@core/services/dialog.service';
import {Configuration} from '@app/shared/models/scheduler-job.models';
import {DeviceProfileService} from '@core/http/device-profile.service';
import {DeviceService} from '@core/http/device.service';

@Component({
  selector: 'tb-scheduler-job-configuration',
  templateUrl: './scheduler-job-configuration.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SchedulerJobConfigurationComponent),
      multi: true
    }
  ]
})
export class SchedulerJobConfigurationComponent implements OnInit, OnDestroy, ControlValueAccessor {

  constructor(protected store: Store<AppState>,
              private utils: UtilsService,
              private dialog: DialogService,
              private translate: TranslateService,
              private deviceService: DeviceService,
              private deviceProfileService: DeviceProfileService,
              private fb: FormBuilder) {
    this.configurationFormGroup = this.fb.group({
      configuration: [null, []]
    });
  }

  @Input()
  disabled: boolean;

  @Input()
  isAdd: boolean;

  @Input()
  jobType: boolean;

  public configurationFormGroup: FormGroup;

  private modelValue: Configuration;

  private propagateChange = (v: any) => {};


  ngOnInit(): void {
    this.configurationFormGroup.valueChanges.subscribe(
      (value) => {
        this.modelValue = value.configuration;
        this.propagateChange(this.modelValue);
    });
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  writeValue(value: Configuration | null ): void {
    this.modelValue = value;
    this.configurationFormGroup.patchValue({
      configuration: value
    }, {emitEvent: false});
  }

  setDisabledState?(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.configurationFormGroup.disable({emitEvent: false});
    } else {
      this.configurationFormGroup.enable({emitEvent: false});
    }

  }

  ngOnDestroy(): void {
  }
}
