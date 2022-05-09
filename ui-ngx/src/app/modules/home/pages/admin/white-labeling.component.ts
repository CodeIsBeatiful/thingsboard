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

import {Component, OnInit} from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { PageComponent } from '@shared/components/page.component';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HasConfirmForm } from '@core/guards/confirm-on-exit.guard';
import { MaterialColorItem } from '@app/shared/models/material.models';
import { WhiteLabeling } from '@shared/models/settings.models';
import { WhiteLabelService } from '@core/http/white-label.service';
import { WhitelabelUtilsService } from '@core/services/whitelabel-utils.service';

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
    // 控件初始化
    this.buildWhiteLabelingSettingsForm();
    this.whiteLabelService.getWhiteLabel().subscribe(
      (whiteLabel) => {
        if (!whiteLabel) {
          whiteLabel = this.whiteLabelUtilsService.getDefaultWhiteLabeling();
        }
        this.whiteLabeling = whiteLabel;
        this.whiteLabelingSettings.reset(this.whiteLabeling);
      }
    );

  }

  buildWhiteLabelingSettingsForm() {
    this.whiteLabelingSettings = this.fb.group({
      appTitle: ['', []],
      faviconUrl: ['', []],
      logoImageUrl: ['', []],
      logoImageHeight: ['', [Validators.min(1), Validators.max(80)]],
      paletteSettings: this.fb.group({
        primaryPalette: ['', []],
        accentPalette: ['', []]
      }),
      showNameVersion: ['', []],
      platformName: ['', []],
      platformVersion: ['', []]
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
    if (this.whiteLabelingSettings.dirty) {
      this.whiteLabelUtilsService.reset();
    }


  }

}
