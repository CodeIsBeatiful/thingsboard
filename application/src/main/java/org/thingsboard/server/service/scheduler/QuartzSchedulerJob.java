package org.thingsboard.server.service.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

@Slf4j
public class QuartzSchedulerJob implements Job {

    private ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    private SchedulerService schedulerService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if(schedulerService == null) {
            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        }
        JobDataMap mergedJobDataMap = jobExecutionContext.getMergedJobDataMap();
        try {
            JsonNode jsonNode = objectMapper.readTree(mergedJobDataMap.get("configuration").toString());
            System.out.println("job schedule !");
        } catch (JsonProcessingException e) {
            log.error("job execute failed, schedulerJobId: {}",jobExecutionContext.getJobDetail().getKey().getName());
        }
    }
}
