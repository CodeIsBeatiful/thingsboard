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

  private propagateChange = (v: any) => {};


  ngOnInit(): void {
    this.attributeUpdateForm.get('originatorId').valueChanges.subscribe(
      (value) => {
        this.modelValue.originatorId = value;
        this.modelValue.msgBody = null;
        this.attributeUpdateForm.patchValue({
            msgBody: null
          }, {emitEvent: false});
      });
    this.attributeUpdateForm.get('msgBody').valueChanges.subscribe(
      (value) => {
        if (this.modelValue.msgBody === value) {
          return;
        }
        this.modelValue.msgBody = value;
        if (value){
          this.propagateChange(this.modelValue);
        }
      });
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  writeValue(value: Configuration | null ): void {
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
      metadata: { scope: 'SERVER_SCOPE'},
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
