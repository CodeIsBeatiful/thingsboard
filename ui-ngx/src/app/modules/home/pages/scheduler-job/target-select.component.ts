import {AfterViewInit, Component, forwardRef, Input, OnInit, Output} from '@angular/core';
import {ControlValueAccessor, FormBuilder, FormGroup, NG_VALUE_ACCESSOR, Validators} from '@angular/forms';
import {Store} from '@ngrx/store';
import {AppState} from '@core/core.state';
import {TranslateService} from '@ngx-translate/core';
import {AliasEntityType, EntityType, entityTypeTranslations} from '@shared/models/entity-type.models';
import {EntityService} from '@core/http/entity.service';
import {EntityId} from '@shared/models/id/entity-id';
import {coerceBooleanProperty} from '@angular/cdk/coercion';
import {BaseData} from '@shared/models/base-data';
import {DeviceProfileService} from '@core/http/device-profile.service';
import {DeviceService} from '@core/http/device.service';
import {Device} from '@shared/models/device.models';

@Component({
  selector: 'tb-target-select',
  templateUrl: './target-select.component.html',
  styleUrls: ['./target-select.component.scss'],
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
      entityType: [null, [Validators.required]],
      entityId: [null, [Validators.required]],
      targetProfileId: [null, [Validators.required]]
    });
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  ngOnInit() {
    this.entitySelectFormGroup.get('targetProfileId').valueChanges.subscribe(
      (value) => {
        // entity type is device profile
        if (value != null) {
          this.modelValue.originatorId = value;
          this.modelValue.refId = value;
          this.propagateChange(this.modelValue);
        } else {
          this.propagateChange(null);
        }
      }
    );
    this.entitySelectFormGroup.get('entityType').valueChanges.subscribe(
      (value) => {
        // select entityId
        this.modelValue.originatorId = null;
        this.modelValue.refId = null;
        this.entitySelectFormGroup.patchValue({
          entityId: null,
          targetProfileId: null
        }, {emitEvent: false});
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
    if (value) {
      this.modelValue = value;
      // if entity is device profile, just set it
      this.entitySelectFormGroup.patchValue({
        entityType: value.originatorId.entityType,
        entityId: value.originatorId,
        targetProfileId: value.refId
      }, {emitEvent: false});
    } else {
      this.modelValue = {
        originatorId: null,
        refId: null
      };
      this.entitySelectFormGroup.patchValue({
        entityType: this.defaultEntityType,
        entityId: null,
        targetProfileId: null
      }, {emitEvent: false});
    }
  }
  // where entity is device,update profile
  updateRef(entity: BaseData<EntityId>) {
    if (entity) {
      if (entity.id.entityType === EntityType.DEVICE) {
        this.modelValue.originatorId = entity.id;
        this.modelValue.refId = (entity as Device).deviceProfileId;
        this.entitySelectFormGroup.patchValue({
          targetProfileId: (entity as Device).deviceProfileId
        }, {emitEvent: false});
        this.propagateChange(this.modelValue);
      }
    } else {
      this.modelValue.originatorId = null;
      this.modelValue.refId = null;
      this.propagateChange(null);
    }
  }
}
