import {Component, OnInit} from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { PageComponent } from '@shared/components/page.component';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {WhiteLabeling} from '@shared/models/settings.models';
import { WhiteLabelService } from '@core/http/white-label.service';
import { HasConfirmForm } from '@core/guards/confirm-on-exit.guard';
import {WhitelabelUtilsService} from "@core/services/whitelabel-utils.service";
import {MaterialColorItem, materialColors} from '@app/shared/models/material.models';

@Component({
  selector: 'tb-white-labeling',
  templateUrl: './white-labeling.component.html',
  styleUrls: ['./white-labeling.component.scss', './settings-card.scss']
})
export class WhiteLabelingComponent extends PageComponent implements OnInit, HasConfirmForm {

  whiteLabelingSettings: FormGroup;

  paletteSettings: FormGroup;

  whiteLabeling: WhiteLabeling;

  materialColors: Array<MaterialColorItem>;

  constructor(protected store: Store<AppState>,
              private router: Router,
              private whiteLabelUtilsService: WhitelabelUtilsService,
              private whiteLabelService: WhiteLabelService,
              public fb: FormBuilder) {
    super(store);
  }


  ngOnInit() {
    //控件初始化
    this.buildWhitelabelingSettingsForm();
    this.whiteLabelService.getWhiteLabel().subscribe(
      (whiteLabel) => {
        this.whiteLabeling = whiteLabel;
        this.whiteLabelingSettings.reset(this.whiteLabeling);
      }
    );

  }

  buildWhitelabelingSettingsForm() {
    this.whiteLabelingSettings = this.fb.group({
      appTitle: ['',[]],
      faviconUrl: ['',[]],
      logoImageUrl:['',[]],
      logoImageHeight:['',[Validators.min(1),Validators.max(80)]],
      paletteSettings:this.fb.group({
        primaryPalette: ['',[]],
        accentPalette: ['',[]]
      }),
      showNameVersion:['',[]],
      platformName: ['',[]],
      platformVersion: ['',[]]
    });
  }

  save(): void {
    this.whiteLabeling = {...this.whiteLabeling, ...this.whiteLabelingSettings.value};
    this.whiteLabelUtilsService.save(this.whiteLabeling);
    this.whiteLabelingSettings.reset(this.whiteLabeling);
  }

  confirmForm(): FormGroup {
    return this.whiteLabelingSettings;
  }

  preview(): void {
    this.whiteLabeling = {...this.whiteLabeling, ...this.whiteLabelingSettings.value};
    this.whiteLabelUtilsService.preview(this.whiteLabeling);
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
    if(this.whiteLabelingSettings.dirty){
      this.whiteLabelUtilsService.reset();
    }


  }

}
