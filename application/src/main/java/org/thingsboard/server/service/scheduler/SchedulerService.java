package org.thingsboard.server.service.scheduler;


import org.thingsboard.server.common.data.id.SchedulerJobId;
import org.thingsboard.server.common.data.scheduler.SchedulerJobInfo;
import org.thingsboard.server.common.msg.queue.TbCallback;
import org.thingsboard.server.gen.transport.TransportProtos;

/**
 * Scheduler service for SchedulerJob
 */
public interface SchedulerService {

    void add(SchedulerJobInfo saveSchedulerJob);

    void update(SchedulerJobInfo saveSchedulerJob);

    void delete(SchedulerJobInfo schedulerJob);

    void onQueueMsg(TransportProtos.SchedulerServiceMsgProto proto, TbCallback callback);

    void process(SchedulerJobId schedulerJobId);
}
