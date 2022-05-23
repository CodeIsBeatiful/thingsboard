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

import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { PageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';
import { SchedulerJob, SchedulerJobInfo} from '@app/shared/models/scheduler-job.models';

@Injectable({
  providedIn: 'root'
})
export class SchedulerJobService {

  constructor(
    private http: HttpClient
  ) { }

  public getTenantSchedulerJobs(pageLink: PageLink, type: string = '', config?: RequestConfig): Observable<PageData<SchedulerJobInfo>> {
    return this.http.get<PageData<SchedulerJobInfo>>(`/api/tenant/schedulerJobs${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  public getSchedulerJob(schedulerJobId: string, config?: RequestConfig): Observable<SchedulerJob> {
    return this.http.get<SchedulerJob>(`/api/schedulerJob/${schedulerJobId}`, defaultHttpOptionsFromConfig(config));
  }

  public saveSchedulerJob(schedulerJob: SchedulerJob, config?: RequestConfig): Observable<SchedulerJob> {
    return this.http.post<SchedulerJob>('/api/schedulerJob', schedulerJob, defaultHttpOptionsFromConfig(config));
  }

  public deleteSchedulerJob(schedulerJobId: string, config?: RequestConfig) {
    return this.http.delete(`/api/schedulerJob/${schedulerJobId}`, defaultHttpOptionsFromConfig(config));
  }
}
