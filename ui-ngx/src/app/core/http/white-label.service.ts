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

import {Injectable} from '@angular/core';
import {defaultHttpOptionsFromConfig, RequestConfig} from './http-utils';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {WhiteLabeling} from '@shared/models/settings.models';

@Injectable({
  providedIn: 'root'
})
export class WhiteLabelService {

  constructor(
    private http: HttpClient
  ) {
  }

  public getWhiteLabel(config?: RequestConfig): Observable<WhiteLabeling> {
    return this.http.get<WhiteLabeling>(`/api/noauth/whiteLabel`, defaultHttpOptionsFromConfig(config));
  }

  public saveWhiteLabel(whiteLabeling: WhiteLabeling,
                        config?: RequestConfig): Observable<WhiteLabeling> {
    return this.http.post<WhiteLabeling>('/api/whiteLabel/whiteLabelParams', whiteLabeling, defaultHttpOptionsFromConfig(config));
  }

  public getAppTheme(whiteLabeling: WhiteLabeling,
                     config?: RequestConfig): Observable<string> {
    return this.http.post('/api/noauth/appThemeCss', whiteLabeling.paletteSettings, {responseType: 'text'});
  }

  public getLoginTheme(whiteLabeling: WhiteLabeling,
                       config?: RequestConfig): Observable<string> {
    return this.http.post('/api/noauth/loginThemeCss', whiteLabeling.paletteSettings, {responseType: 'text'});
  }

}
