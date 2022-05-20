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
import {EntityType} from '@shared/models/entity-type.models';
import {OtaUpdateType} from '@shared/models/ota-package.models';
import {DeviceProfileService} from '@core/http/device-profile.service';
import {DeviceService} from '@core/http/device.service';

@Component({
  selector: 'tb-scheduler-job-ota-update',
  templateUrl: './scheduler-job-ota-update.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SchedulerJobOtaUpdateComponent),
      multi: true
    }
  ]
})
export class SchedulerJobOtaUpdateComponent implements OnInit, OnDestroy, ControlValueAccessor {

  constructor(protected store: Store<AppState>,
              private utils: UtilsService,
              private dialog: DialogService,
              private translate: TranslateService,
              private deviceService: DeviceService,
              private deviceProfileService: DeviceProfileService,
              private fb: FormBuilder) {
    this.otaUpdateForm = this.fb.group({
      originatorId: [null, [Validators.required]],
      packageId: [null, [Validators.required]],
    });
  }

  @Input()
  disabled: boolean;

  private schedulerJobType: string;

  get jobType(): string {
    return this.schedulerJobType;
  }

  @Input()
  set jobType(value) {
    this.schedulerJobType = value;
    this.msgType = this.msgTypeMap.get(this.jobType);
  }

  private msgType: string;

  allowedEntityTypes = [EntityType.DEVICE, EntityType.DEVICE_PROFILE];

  private msgTypeMap = MsgTypeMap;

  otaUpdateType = OtaUpdateType;

  public otaUpdateForm: FormGroup;

  refId: string;

  private modelValue: Configuration;

  private propagateChange = (v: any) => { };

  ngOnInit(): void {
    this.otaUpdateForm.get('originatorId').valueChanges.subscribe(
      (value) => {
        this.modelValue.originatorId = value;
        if (value) {
          if (value.entityType === EntityType.DEVICE) {
            this.deviceService.getDevice(value.id).subscribe(
              (device) => {
                this.refId = device.deviceProfileId.id;
              });
          } else if (value.entityType === EntityType.DEVICE_PROFILE) {
            this.refId = value.id;
          } else {
            this.refId = null;
          }
        } else {
          this.refId = null;
        }
        this.propagateChange(null);
      });
    this.otaUpdateForm.get('packageId').valueChanges.subscribe(
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

  async writeValue(value: Configuration | null): Promise<void> {
    if (value != null) {
      // because reload write other type
      if (value.msgType !== this.msgType) {
        return;
      }
      this.modelValue = value;
      if (this.modelValue.originatorId.entityType === EntityType.DEVICE) {
        const device = await this.deviceService.getDevice(this.modelValue.originatorId.id).toPromise();
        this.refId = device.deviceProfileId.id;
      } else if (this.modelValue.originatorId.entityType === EntityType.DEVICE_PROFILE) {
        this.refId = this.modelValue.originatorId.id;
      }
      this.otaUpdateForm.patchValue({
        originatorId: value.originatorId,
        packageId: value.msgBody
      }, {emitEvent: false});
    } else {
      this.reset();
    }
  }

  reset(): void {
    this.otaUpdateForm.patchValue({
      originatorId: null,
      packageId: null,
    }, {emitEvent: false});
    this.refId = null;
    this.modelValue = {
      metadata: {},
      msgBody: null,
      msgType: this.msgType,
      originatorId: null
    };
  }

  setDisabledState?(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.otaUpdateForm.disable({emitEvent: false});
    } else {
      this.otaUpdateForm.enable({emitEvent: false});
    }

  }

  ngOnDestroy(): void {
  }
}
