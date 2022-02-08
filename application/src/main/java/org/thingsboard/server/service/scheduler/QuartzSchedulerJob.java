package org.thingsboard.server.service.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.thingsboard.server.common.data.id.SchedulerJobId;

@Slf4j
public class QuartzSchedulerJob implements Job {


    @Autowired
    private SchedulerService schedulerService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (schedulerService == null) {
            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        }
        try {
            SchedulerService schedulerService = (SchedulerService) jobExecutionContext.getScheduler().getContext().get("schedulerService");
            String strSchedulerJobId = jobExecutionContext.getJobDetail().getKey().getName();
            SchedulerJobId schedulerJobId = SchedulerJobId.fromString(strSchedulerJobId);
            log.trace("triggered job id:[{}]", strSchedulerJobId);
            schedulerService.process(schedulerJobId);
        } catch (SchedulerException e) {
           log.error("can't get scheduler context");
        }


    }


}
