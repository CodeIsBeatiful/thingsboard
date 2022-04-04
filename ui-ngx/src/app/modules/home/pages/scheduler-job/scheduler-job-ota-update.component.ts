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
import {EntityId, entityIdEquals} from '@shared/models/id/entity-id';
import {DeviceProfileId} from '@shared/models/id/device-profile-id';
import {NULL_UUID} from '@shared/models/id/has-uuid';

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

  refId: EntityId = new DeviceProfileId(NULL_UUID);

  private modelValue: Configuration;

  private propagateChange = (v: any) => {};

  ngOnInit(): void {
    this.otaUpdateForm.get('originatorId').valueChanges.subscribe(
      (value) => {
        if (value){
          this.modelValue.originatorId = value.originatorId;
          this.refId = value.refId;
        }else{
          this.modelValue.originatorId = null;
          this.refId = new DeviceProfileId(NULL_UUID);
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

  async writeValue(value: Configuration | null ): Promise<void> {
    if (value != null) {
      // because reload write other type
      if (value.msgType !== this.msgType) {
        return;
      }
      this.modelValue = value;
      if (this.modelValue.originatorId.entityType === EntityType.DEVICE ) {
        const device = await this.deviceService.getDevice(this.modelValue.originatorId.id).toPromise();
        this.refId = device.deviceProfileId;
        this.otaUpdateForm.patchValue({
          originatorId: {
            originatorId: this.modelValue.originatorId,
            refId: this.refId
          }
        }, {emitEvent: false});
      } else if (this.modelValue.originatorId.entityType === EntityType.DEVICE_PROFILE ){
        this.refId = this.modelValue.originatorId;
        this.otaUpdateForm.patchValue({
          originatorId: {
            originatorId: this.modelValue.originatorId,
            refId: this.refId
          }
        }, {emitEvent: false});
      }
      this.otaUpdateForm.patchValue({
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
    this.refId = new DeviceProfileId(NULL_UUID);
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

  // // target-select send
  // updateRef(entityId: EntityId | null) {
  //   if (entityIdEquals(this.refId, entityId)) {
  //     return;
  //   }
  //   this.refId = entityId;
  //   // reset msgBody
  //   this.modelValue.msgBody = null;
  //   this.otaUpdateForm.patchValue({
  //     msgBody: null
  //   }, {emitEvent: false});
  // }

  ngOnDestroy(): void {
  }
}
