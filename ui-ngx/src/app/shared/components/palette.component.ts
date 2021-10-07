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
import {PageComponent} from "@shared/components/page.component";
import {UtilsService} from "@core/services/utils.service";
import {DialogService} from "@core/services/dialog.service";
import {MaterialColorItem, materialColors} from "@shared/models/material.models";
import {Palette} from "@shared/models/settings.models";

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
  label: String;

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
      type:  ['',[]]
    });
    const NullColors = {
      value: null,
      group: null,
      label: "",
      isDark: false,
    }
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

  public getValueByGroup(group: String):String{
    for (let materialColor of this.materialColors) {
      if(materialColor.group === group){
        return materialColor.value;
      }
    }
    return "";
  }
}
