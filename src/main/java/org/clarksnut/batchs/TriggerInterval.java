package org.clarksnut.batchs;

import javax.enterprise.concurrent.LastExecution;
import javax.enterprise.concurrent.Trigger;
import java.time.*;
import java.util.Calendar;
import java.util.Date;

public class TriggerInterval implements Trigger {

    private Duration interval;

    public TriggerInterval(Duration interval) {
        this.interval = interval;
    }

    public Date getNextRunTime(LastExecution lastExecutionInfo, Date taskScheduledTime) {
        if (lastExecutionInfo == null) {
            LocalTime origin = LocalTime.MIDNIGHT;
            LocalTime now = LocalTime.now();

            Duration duration = Duration.between(origin, now);

            long sinceOrigin = duration.toMillis();
            long millisPerInterval = interval.toMillis();


            long millisSinceIntervalStart = sinceOrigin % millisPerInterval;
            LocalTime startNext = now
                    // Back to interval init
                    .minusNanos(millisSinceIntervalStart * 1_000_000)
                    // Add interval
                    .plusSeconds(interval.toMinutes() * 60);

            LocalDateTime nowLocalDateTime = LocalDateTime.now();
            LocalDateTime startNextLocalDateTime = startNext.atDate(LocalDate.now());
            if (Duration.between(startNextLocalDateTime, nowLocalDateTime).toDays() > 0) {
                startNextLocalDateTime = startNext.atDate(nowLocalDateTime.toLocalDate().plusDays(1));
            }


            Instant instant = startNextLocalDateTime.atZone(ZoneId.systemDefault()).toInstant();
            return Date.from(instant);
        }

        Date scheduledStart = lastExecutionInfo.getScheduledStart();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(scheduledStart);
        calendar.add(Calendar.MILLISECOND, interval.getNano() * 1_000_000);

        return calendar.getTime();
    }

    public boolean skipRun(LastExecution lastExecutionInfo, Date scheduledRunTime) {
        return false;
    }

}
