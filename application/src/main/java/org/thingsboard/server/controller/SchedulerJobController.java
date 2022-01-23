package org.thingsboard.server.controller;


import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.SchedulerJobId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.scheduler.SchedulerJob;
import org.thingsboard.server.common.data.scheduler.SchedulerJobInfo;
import org.thingsboard.server.dao.scheduler.SchedulerJobService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.scheduler.SchedulerService;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import static org.thingsboard.server.controller.ControllerConstants.*;
import static org.thingsboard.server.dao.service.Validator.validateId;


@Slf4j
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class SchedulerJobController extends BaseController{

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private SchedulerJobService schedulerJobService;

    public static final String SCHEDULER_JOB_ID = "schedulerJobId";


    @ApiOperation(value = "Get SchedulerJobInfo (getSchedulerJobInfoById)",
            notes = "Fetch the SchedulerJobInfo object based on the provided SchedulerJob Id. " +
                    TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = {"/schedulerJob/info/{schedulerJobId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public SchedulerJobInfo getSchedulerJobInfoById(@PathVariable("schedulerJobId") String strSchedulerJobId) throws ThingsboardException {
        checkParameter(SCHEDULER_JOB_ID, strSchedulerJobId);
        try {
            SchedulerJobId schedulerJobId = new SchedulerJobId(toUUID(strSchedulerJobId));
            return checkSchedulerJobId(schedulerJobId, Operation.READ);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Get SchedulerJob (getSchedulerJobById)",
            notes = "Fetch the SchedulerJob object based on the provided SchedulerJob Id. " +
                    TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = {"/schedulerJob/{schedulerJobId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public SchedulerJob getSchedulerJobById(@PathVariable("schedulerJobId") String strSchedulerJobId) throws ThingsboardException {
        checkParameter(SCHEDULER_JOB_ID, strSchedulerJobId);
        try {
            SchedulerJobId schedulerJobId = new SchedulerJobId(toUUID(strSchedulerJobId));
            return checkSchedulerJobId(schedulerJobId, Operation.READ);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Create Or Update SchedulerJob (saveSchedulerJob)",
            notes = "Create or update the SchedulerJob. When creating SchedulerJob, platform generates SchedulerJob Id as " + UUID_WIKI_LINK +
                    "The newly created SchedulerJob id will be present in the response. " +
                    "Specify existing SchedulerJob id to update the SchedulerJob. " +
                    "Referencing non-existing SchedulerJob Id will cause 'Not Found' error." +
                    TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = {"/schedulerJob"}, method = {RequestMethod.POST})
    @ResponseBody
    public SchedulerJob saveSchedulerJob(@RequestBody SchedulerJob schedulerJob) throws ThingsboardException {
        log.trace("saveSchedulerJob {}", schedulerJob);
        try {
            schedulerJob.setTenantId(getCurrentUser().getTenantId());
            checkEntity(schedulerJob.getId(), schedulerJob, Resource.SCHEDULER_JOB);
            SchedulerJob saveSchedulerJob = checkNotNull(this.schedulerJobService.saveSchedulerJob(schedulerJob));
            logEntityAction(saveSchedulerJob.getId(), saveSchedulerJob, saveSchedulerJob
                            .getCustomerId(),
                    (schedulerJob.getId() == null) ? ActionType.ADDED : ActionType.UPDATED, null, new Object[0]);
            if (schedulerJob.getId() == null) {
                this.schedulerService.add(saveSchedulerJob);
            } else {
                this.schedulerService.update(saveSchedulerJob);
            }
            return saveSchedulerJob;
        } catch (Exception e) {
            log.warn("Failed to save or update schedulerJob " + schedulerJob, e);
            logEntityAction(emptyId(EntityType.SCHEDULER_JOB), schedulerJob, null,
                    (schedulerJob.getId() == null) ? ActionType.ADDED : ActionType.UPDATED, e, new Object[0]);
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Delete SchedulerJob (deleteSchedulerJob)",
            notes = "Deletes the SchedulerJob." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = {"/schedulerJob/{schedulerJobId}"}, method = {RequestMethod.DELETE})
    @ResponseStatus(HttpStatus.OK)
    public void deleteSchedulerJob(@PathVariable("schedulerJobId") String strSchedulerJobId) throws ThingsboardException {
        checkParameter(SCHEDULER_JOB_ID, strSchedulerJobId);
        try {
            SchedulerJobId schedulerJobId = new SchedulerJobId(toUUID(strSchedulerJobId));
            SchedulerJob schedulerJob = checkSchedulerJobId(schedulerJobId, Operation.DELETE);
            this.schedulerJobService.deleteSchedulerJob(getTenantId(), schedulerJobId);
            logEntityAction(schedulerJobId, schedulerJob, schedulerJob
                    .getCustomerId(), ActionType.DELETED, null, new Object[] { strSchedulerJobId });
            this.schedulerService.delete(schedulerJob);
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.SCHEDULER_JOB), null, null, ActionType.DELETED, e, new Object[] { strSchedulerJobId });
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Get Tenant SchedulerJobs (getTenantSchedulerJobs)",
            notes = "Returns a page of schedulerJobs owned by tenant. " +
                    PAGE_DATA_PARAMETERS + TENANT_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = {"/tenant/schedulerJobs"}, params = {"pageSize", "page"}, method = {RequestMethod.GET})
    @ResponseBody
    public PageData<SchedulerJobInfo> getTenantSchedulerJobs(@RequestParam int pageSize, @RequestParam int page, @RequestParam(required = false) String textSearch, @RequestParam(required = false) String sortProperty, @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            if(sortProperty == null && sortOrder == null){
                sortProperty = "createdTime";
                sortOrder = "desc";
            }
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            return checkNotNull(this.schedulerJobService.findSchedulerJobsByTenantId(tenantId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }
}
