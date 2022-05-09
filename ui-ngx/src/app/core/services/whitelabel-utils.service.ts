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

import {DomSanitizer} from '@angular/platform-browser';
import {Inject, Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';

import {LocalStorageService} from '@core/local-storage/local-storage.service';
import {AdminService} from '@core/http/admin.service';
import {DOCUMENT} from '@angular/common';
import {WhiteLabelService} from '@core/http/white-label.service';
import {Palette, WhiteLabeling} from '@shared/models/settings.models';
import {whiteLabeling as wl} from '@global/white-labeling';
import {ActionSettingsChangeLanguage} from '@core/settings/settings.actions';
import {select, Store} from '@ngrx/store';
import {AppState} from '@core/core.state';
import {map, switchMap, take} from 'rxjs/operators';
import {selectAuth} from '@core/auth/auth.selectors';
import {Observable, of, ReplaySubject} from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class WhitelabelUtilsService {

  whiteLabeling: WhiteLabeling;

  loginThemeCss: string;

  appThemeCss: string;

  loginThemeCssElm: any;

  appThemeCssElm: any;


  constructor(
    private adminService: AdminService,
    private storageService: LocalStorageService,
    private router: Router,
    private translate: TranslateService,
    private sanitizer: DomSanitizer,
    @Inject(DOCUMENT) private document: HTMLDocument,
    private store: Store<AppState>,
    private whiteLabelService: WhiteLabelService
  ) {
  }

  mergeWithConst(whiteLabeling: WhiteLabeling): WhiteLabeling{
    if (!whiteLabeling.appTitle) {
      whiteLabeling.appTitle = wl.appTitle;
    }
    if (!whiteLabeling.logoImageUrl) {
      whiteLabeling.logoImageUrl = wl.logoImageUrl;
    }
    if (!whiteLabeling.logoImageHeight) {
      whiteLabeling.logoImageHeight = wl.logoImageHeight;
    }
    if (!whiteLabeling.faviconUrl) {
      whiteLabeling.faviconUrl = wl.faviconUrl;
    }
    if (!whiteLabeling.showNameVersion) {
      whiteLabeling.showNameVersion = wl.showNameVersion;
    }
    if (!whiteLabeling.platformName) {
      whiteLabeling.platformName = wl.platformName;
    }
    if (!whiteLabeling.platformVersion) {
      whiteLabeling.platformVersion = wl.platformVersion;
    }
    return whiteLabeling;
  }

  setup(): Observable<WhiteLabeling>{
    return this.whiteLabelService.getWhiteLabel().pipe(
      map((whiteLabel) => {
        if (!whiteLabel) {
          whiteLabel = this.getDefaultWhiteLabeling();
        }
        this.whiteLabeling = this.mergeWithConst(whiteLabel);
        this.process(true);
        return whiteLabel;
      }),
      switchMap((whiteLabel) => {
        const whiteLabelSubject = new ReplaySubject<WhiteLabeling>();
        this.setupAppTheme().subscribe(() => {
          whiteLabelSubject.next(whiteLabel);
        });
        return whiteLabelSubject;
      }),
      switchMap((whiteLabel) => {
        const whiteLabelSubject = new ReplaySubject<WhiteLabeling>();
        this.setupLoginTheme().subscribe(() => {
          whiteLabelSubject.next(whiteLabel);
        });
        return whiteLabelSubject;
      })
    );
  }

  getDefaultWhiteLabeling(): WhiteLabeling {
    return {
      appTitle: null,
      customCss: null,
      enableHelpLinks: false,
      faviconUrl: null,
      helpLinkBaseUrl: null,
      logoImageHeight: null,
      logoImageUrl: null,
      paletteSettings: {
        primaryPalette: null,
        accentPalette: null
      },
      platformName: null,
      platformVersion: null,
      showNameVersion: false,
      whiteLabelingEnabled: true
    };
  }

  setupLoginTheme(): Observable<void> {
    this.addLoginTheme();
    return this.whiteLabelService.getLoginTheme(this.whiteLabeling).pipe(
      map((loginThemeCss) => {
        if (loginThemeCss){
          this.loginThemeCss = loginThemeCss;
          this.loginThemeCssElm.innerText = this.loginThemeCss.toString();
        }
      }));
  }


  setupAppTheme(): Observable<void> {
    this.addAppTheme();
    return this.whiteLabelService.getAppTheme(this.whiteLabeling).pipe(
      map((appThemeCss) => {
        if (appThemeCss) {
          this.appThemeCss = appThemeCss;
          this.appThemeCssElm.innerText = this.appThemeCss.toString();
        }
      }));
  }

  public addLoginTheme() {
    if (!this.loginThemeCssElm){
      let loginThemeCssElm = this.document.getElementById('tb-login-theme');
      if (!loginThemeCssElm){
        loginThemeCssElm = this.document.createElement('style');
        loginThemeCssElm.id = 'tb-login-theme';
        const head = this.document.getElementsByTagName('head')[0];
        head.appendChild(loginThemeCssElm);
      }
      this.loginThemeCssElm = loginThemeCssElm;
    }
  }


  public addAppTheme(){
    if (!this.appThemeCssElm){
      let appThemeCssElm = this.document.getElementById('tb-app-theme');
      if (!appThemeCssElm){
        appThemeCssElm = this.document.createElement('style');
        appThemeCssElm.id = 'tb-app-theme';
        const head = this.document.getElementsByTagName('head')[0];
        head.appendChild(appThemeCssElm);
      }
      this.appThemeCssElm = appThemeCssElm;
    }
  }


  preview(whiteLabel: WhiteLabeling) {
    this.whiteLabeling = this.mergeWithConst(whiteLabel);
    this.process();
    this.whiteLabelService.getAppTheme(this.whiteLabeling).subscribe(
      (appThemeCss) => {
        if (appThemeCss){
          this.appThemeCss = appThemeCss;
          this.appThemeCssElm.innerText = this.appThemeCss.toString();
        }
      }
    );
  }

  save(whiteLabel: WhiteLabeling) {
    this.whiteLabelService.saveWhiteLabel(whiteLabel).subscribe(
      (whiteLabeling) => {
        this.whiteLabeling = this.mergeWithConst(whiteLabeling);
        this.process();
        this.setupAppTheme().subscribe();
        this.setupLoginTheme().subscribe();
      }
    );
  }

  reset() {
    this.setup().subscribe();
  }

  private process(isSetup?: boolean) {
    // set title
    if (!isSetup) {
      this.store.pipe(select(selectAuth), take(1)).subscribe(
        val => {
          if (val.userDetails) {
            this.store.dispatch(new ActionSettingsChangeLanguage({userLang: val.userDetails.additionalInfo.userLang}));
          }
        }
      );
    }
    // set icon
    this.document.querySelector(('link[rel*=\'icon\']')).setAttribute('href', this.whiteLabeling.faviconUrl);
  }
}
