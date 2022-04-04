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
