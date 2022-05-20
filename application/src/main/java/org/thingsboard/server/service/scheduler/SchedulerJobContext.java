/**
 * Copyright © 2016-2022 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.service.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableScheduledFuture;
import org.thingsboard.server.common.data.scheduler.SchedulerJob;

import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Scheduler job context
 */
public class SchedulerJobContext {

    private final SchedulerJob schedulerJob;

    private final long startTime;

    private final String timezone;

    private final long endTime;

    private final boolean repeat;

    private final String repeatType;

    private final JsonNode repeatJsonNode;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private volatile ListenableScheduledFuture<?> scheduledFuture;

    public SchedulerJobContext(SchedulerJob schedulerJob) {
        this.schedulerJob = schedulerJob;
        JsonNode scheduleJsonNode = schedulerJob.getSchedule();
        this.startTime = scheduleJsonNode.get("startTime").asLong();
        this.timezone = scheduleJsonNode.get("timezone").asText();
        this.repeatJsonNode = scheduleJsonNode.get("repeat");
        this.repeat = this.repeatJsonNode.isNull() ? false : true;
        this.repeatType = this.repeat ? this.repeatJsonNode.get("type").asText() : null;
        this.endTime = this.repeat ? this.repeatJsonNode.get("endTime").asLong() : 0;
    }

    public SchedulerJob getSchedulerJob() {
        return schedulerJob;
    }

    public ListenableScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ListenableScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public long getNextTime(long ts) {
        long tmp = startTime;
        if (this.repeat && this.endTime > ts) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
            switch (repeatType) {
                case "DAILY":
                    for (; tmp < this.endTime; tmp += 24 * 60 * 60 * 1000) {
                        if (tmp > ts) {
                            return tmp;
                        }
                    }
                    break;
                case "WEEKLY":
                    Set repeatDays = objectMapper.convertValue(repeatJsonNode.get("repeatOn"), Set.class);
                    for (; tmp < this.endTime; tmp += 24 * 60 * 60 * 1000) {
                        if (tmp > ts) {
                            calendar.setTimeInMillis(tmp);
                            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                            dayOfWeek--;
                            if (repeatDays.contains(dayOfWeek)) {
                                return tmp;
                            }
                        }
                    }
                    break;
                case "MONTHLY":
                    for (int repeatIteration = 0; tmp < endTime; repeatIteration++) {
                        calendar.setTimeInMillis(startTime);
                        calendar.add(Calendar.MONTH, repeatIteration);
                        tmp = calendar.getTimeInMillis();
                        if (tmp > ts) {
                            return tmp;
                        }
                    }
                    break;
                case "YEARLY":
                    for (int repeatIteration = 0; tmp < endTime; repeatIteration++) {
                        calendar.setTimeInMillis(startTime);
                        calendar.add(Calendar.YEAR, repeatIteration);
                        tmp = calendar.getTimeInMillis();
                        if (tmp > ts) {
                            return tmp;
                        }
                    }
                    break;
                case "TIMER":
                    String timeUnit = this.repeatJsonNode.get("timeUnit").asText();
                    long interval = this.repeatJsonNode.get("repeatInterval").asLong();
                    long timeStep = TimeUnit.valueOf(timeUnit).toMillis(interval);
                    for (; tmp < this.endTime; tmp += timeStep) {
                        if (tmp > ts) {
                            return tmp;
                        }
                    }
                    break;
                default:
                    break;
            }
            return 0;
        } else {
            return ts < this.startTime ? this.startTime : 0;
        }
    }
}
