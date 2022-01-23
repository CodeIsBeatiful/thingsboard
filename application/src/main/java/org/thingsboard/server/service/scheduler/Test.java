package org.thingsboard.server.service.scheduler;

import org.thingsboard.server.common.data.id.SchedulerJobId;
import org.thingsboard.server.common.data.scheduler.SchedulerJob;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Test {
    public static void main(String[] args) {
        //do nothing
        UUID uuid = UUID.randomUUID();
        UUID uuid2 = new UUID(uuid.getMostSignificantBits(),uuid.getLeastSignificantBits());
        ConcurrentHashMap<String, Set<SchedulerJobId>> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 5; i++) {
            map.computeIfAbsent(i+"", value-> {
                HashSet<SchedulerJobId> set = new HashSet<>();
                set.add(new SchedulerJobId(uuid));
                return set;
            });
        }

        map.values().stream().forEach(set->{
            set.remove(new SchedulerJobId(uuid));
            return;
        });

        HashSet<String> strings = new HashSet<>();
        strings.add("1");
        strings.add("1");

        System.out.println(strings.size());

    }
}
