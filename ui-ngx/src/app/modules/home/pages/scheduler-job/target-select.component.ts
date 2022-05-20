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

import {AfterViewInit, Component, forwardRef, Input, OnInit, Output} from '@angular/core';
import {ControlValueAccessor, FormBuilder, FormGroup, NG_VALUE_ACCESSOR, Validators} from '@angular/forms';
import {Store} from '@ngrx/store';
import {AppState} from '@core/core.state';
import {TranslateService} from '@ngx-translate/core';
import {AliasEntityType, EntityType, entityTypeTranslations} from '@shared/models/entity-type.models';
import {EntityService} from '@core/http/entity.service';
import {coerceBooleanProperty} from '@angular/cdk/coercion';
import {DeviceProfileService} from '@core/http/device-profile.service';
import {DeviceService} from '@core/http/device.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'tb-target-select',
  templateUrl: './target-select.component.html',
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => TargetSelectComponent),
    multi: true
  }]
})
export class TargetSelectComponent implements ControlValueAccessor, OnInit, AfterViewInit {

  entitySelectFormGroup: FormGroup;

  modelValue: any;

  entityTypeTranslations = entityTypeTranslations;

  EntityType = EntityType;

  allowedEntityTypes: Array<EntityType>;

  private requiredValue: boolean;
  private deviceIdChange$: Subscription;
  private profileIdChange$: Subscription;

  get required(): boolean {
    return this.requiredValue;
  }

  @Input()
  set required(value: boolean) {
    this.requiredValue = coerceBooleanProperty(value);
  }

  @Input()
  disabled: boolean;

  displayEntityTypeSelect = true;

  private readonly defaultEntityType: EntityType | AliasEntityType = null;

  private propagateChange = (v: any) => { };

  constructor(private store: Store<AppState>,
              private entityService: EntityService,
              public translate: TranslateService,
              public deviceService: DeviceService,
              public deviceProfileService: DeviceProfileService,
              private fb: FormBuilder) {

    this.allowedEntityTypes = Array.of(EntityType.DEVICE, EntityType.DEVICE_PROFILE);

    this.defaultEntityType = this.allowedEntityTypes[0];

    this.entitySelectFormGroup = this.fb.group({
      entityType: [null, [Validators.required]]
    });
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  ngOnInit() {
    this.entitySelectFormGroup.get('entityType').valueChanges.subscribe(
      (value) => {
        this.modelValue = {
          entityType: value,
          id: null
        };
        this.entitySelectFormGroup.patchValue({
          entityType: value,
          entityId: null
        }, {emitEvent: false});
        this.destroyFormObservable();
        this.entitySelectFormGroup.removeControl('deviceId');
        this.entitySelectFormGroup.removeControl('profileId');
        if (value === EntityType.DEVICE) {
          this.entitySelectFormGroup.addControl('deviceId',
            this.fb.control(null, [Validators.required]));
          this.initDeviceIdObservable();
        } else if (value === EntityType.DEVICE_PROFILE) {
          this.entitySelectFormGroup.addControl('profileId',
            this.fb.control(null, [Validators.required]));
          this.initProfileIdObservable();
        }
        this.propagateChange(null);
      }
    );
  }

  ngAfterViewInit(): void {
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.entitySelectFormGroup.disable({emitEvent: false});
    } else {
      this.entitySelectFormGroup.enable({emitEvent: false});
    }
  }

  writeValue(value: any | null): void {
    this.destroyFormObservable();
    this.entitySelectFormGroup.removeControl('deviceId');
    this.entitySelectFormGroup.removeControl('profileId');
    if (value) {
      this.modelValue = value;
      // if entity is device profile, just set it
      if (value.entityType === EntityType.DEVICE) {
        this.entitySelectFormGroup.addControl('deviceId',
          this.fb.control(value.id, [Validators.required]));
        this.initDeviceIdObservable();
      } else if (value.entityType === EntityType.DEVICE_PROFILE) {
        this.entitySelectFormGroup.addControl('profileId',
          this.fb.control(value, [Validators.required]));
        this.initProfileIdObservable();
      }
      this.entitySelectFormGroup.patchValue({
        entityType: value.entityType
      }, {emitEvent: false});
    } else {
      this.modelValue = {
        entityType: this.defaultEntityType,
        id: null
      };
      this.entitySelectFormGroup.addControl('deviceId',
        this.fb.control(null, [Validators.required]));
      this.initDeviceIdObservable();
      this.entitySelectFormGroup.patchValue({
        entityType: this.defaultEntityType
      }, {emitEvent: false});
    }

  }

  private destroyFormObservable() {
    if (this.deviceIdChange$) {
      this.deviceIdChange$.unsubscribe();
    }
    if (this.profileIdChange$) {
      this.profileIdChange$.unsubscribe();
    }
  }

  private initDeviceIdObservable() {
    this.deviceIdChange$ = this.entitySelectFormGroup.get('deviceId').valueChanges.subscribe(
      (value) => {
        this.modelValue.id = value;
        if (value) {
          this.propagateChange(this.modelValue);
        } else {
          this.propagateChange(null);
        }
      });
  }

  private initProfileIdObservable() {
    this.profileIdChange$ = this.entitySelectFormGroup.get('profileId').valueChanges.subscribe(
      (value) => {
        this.modelValue.id = value.id;
        if (value) {
          this.propagateChange(this.modelValue);
        } else {
          this.propagateChange(null);
        }
      });
  }
}
