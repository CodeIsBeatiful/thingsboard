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
import {ControlValueAccessor, FormBuilder, FormGroup, NG_VALUE_ACCESSOR, Validators} from '@angular/forms';
import {Store} from '@ngrx/store';
import {AppState} from '@app/core/core.state';
import {TranslateService} from '@ngx-translate/core';
import {UtilsService} from '@core/services/utils.service';
import {DialogService} from '@core/services/dialog.service';
import {Configuration, MsgTypeMap} from '@app/shared/models/scheduler-job.models';
import {DeviceProfileService} from '@core/http/device-profile.service';
import {DeviceService} from '@core/http/device.service';

@Component({
  selector: 'tb-scheduler-job-attribute-update',
  templateUrl: './scheduler-job-attribute-update-component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SchedulerJobAttributeUpdateComponent),
      multi: true
    }
  ]
})
export class SchedulerJobAttributeUpdateComponent implements OnInit, OnDestroy, ControlValueAccessor {

  constructor(protected store: Store<AppState>,
              private utils: UtilsService,
              private dialog: DialogService,
              private translate: TranslateService,
              private deviceService: DeviceService,
              private deviceProfileService: DeviceProfileService,
              private fb: FormBuilder) {
    this.attributeUpdateForm = this.fb.group({
      originatorId: [null, [Validators.required]],
      msgType: [null, []],
      msgBody: [null, [Validators.required]],
    });
  }

  @Input()
  disabled: boolean;

  private msgType: string;

  private msgTypeMap = MsgTypeMap;

  private schedulerJobType: string;

  get jobType(): string {
    return this.schedulerJobType;
  }

  @Input()
  set jobType(value) {
    this.schedulerJobType = value;
    this.msgType = this.msgTypeMap.get(this.jobType);
  }

  public attributeUpdateForm: FormGroup;

  private modelValue: Configuration;

  private propagateChange = (v: any) => { };


  ngOnInit(): void {
    this.attributeUpdateForm.get('originatorId').valueChanges.subscribe(
      (value) => {
        this.modelValue.originatorId = value;
        this.modelValue.msgBody = null;
        this.attributeUpdateForm.patchValue({
          msgBody: null
        }, {emitEvent: false});
        this.propagateChange(null);
      });
    this.attributeUpdateForm.get('msgBody').valueChanges.subscribe(
      (value) => {
        this.modelValue.msgBody = value;
        if (value) {
          this.propagateChange(this.modelValue);
        } else {
          this.propagateChange(null);
        }
      });
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  writeValue(value: Configuration | null): void {
    if (value != null) {
      // because reload write other type
      if (value.msgType !== this.msgType) {
        return;
      }
      this.modelValue = value;
      this.attributeUpdateForm.patchValue(this.modelValue, {emitEvent: false});
    } else {
      this.reset();
    }
  }


  reset(): void {
    this.msgType = this.msgTypeMap.get(this.jobType);
    this.modelValue = {
      metadata: {scope: 'SERVER_SCOPE'},
      msgBody: null,
      msgType: this.msgType,
      originatorId: null
    };
    this.attributeUpdateForm.patchValue(this.modelValue, {emitEvent: false});
  }

  setDisabledState?(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.attributeUpdateForm.disable({emitEvent: false});
    } else {
      this.attributeUpdateForm.enable({emitEvent: false});
    }
  }

  ngOnDestroy(): void {
  }
}
