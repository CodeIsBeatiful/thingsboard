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
  ControlValueAccessor,
  FormBuilder,
  FormGroup,
  NG_VALUE_ACCESSOR
} from '@angular/forms';
import {Store} from '@ngrx/store';
import {AppState} from '@app/core/core.state';
import {TranslateService} from '@ngx-translate/core';
import {PageComponent} from '@shared/components/page.component';
import {UtilsService} from '@core/services/utils.service';
import {DialogService} from '@core/services/dialog.service';
import {MaterialColorItem, materialColors} from '@shared/models/material.models';
import {Palette} from '@shared/models/settings.models';

@Component({
  selector: 'tb-palette',
  templateUrl: './palette.component.html',
  styleUrls: ['./palette.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PaletteComponent),
      multi: true
    }
  ]
})
export class PaletteComponent extends PageComponent implements OnInit, OnDestroy, ControlValueAccessor {


  @Input()
  label: string;

  public paletteFormGroup: FormGroup;

  public materialColors: Array<MaterialColorItem>;

  private propagateChange = null;

  private palette: Palette;

  constructor(protected store: Store<AppState>,
              private utils: UtilsService,
              private dialog: DialogService,
              private translate: TranslateService,
              private fb: FormBuilder) {
    super(store);
  }

  ngOnInit(): void {
    this.paletteFormGroup = this.fb.group({
      type:  ['', []]
    });
    const NullColors = {
      value: null,
      group: null,
      label: '',
      isDark: false,
    };
    this.materialColors = [NullColors, ...materialColors];

    this.paletteFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  writeValue(palette: Palette): void {
    if (palette) {
      this.palette = palette;
    } else {
      this.palette = {
        colors: null,
        extends: null,
        type: null
      };
    }
    this.paletteFormGroup.patchValue(
      { type:  this.palette.type }, {emitEvent: false}
    );
  }


  private updateModel() {
    const tempType = this.paletteFormGroup.get('type').value;
    if (this.palette.type !== tempType) {
      this.palette.type = tempType;
      this.propagateChange(this.palette);
    }
  }

  public getValueByGroup(group: string): string{
    for (const materialColor of this.materialColors) {
      if (materialColor.group === group) {
        return materialColor.value;
      }
    }
    return '';
  }
}
