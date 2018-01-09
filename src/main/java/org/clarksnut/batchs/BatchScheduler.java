package org.clarksnut.batchs;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

@Startup
@Singleton
public class BatchScheduler {

    @Inject
    @ConfigurationValue("clarksnut.scheduler.pull.interval")
    private Optional<Integer> clarksnutSchedulerPullInterval;

    @Resource
    private ManagedScheduledExecutorService scheduler;

    @PostConstruct
    private void init() {
        Duration interval = Duration.ofMinutes(clarksnutSchedulerPullInterval.orElse(15));
        scheduler.schedule(this::invokeBatchs, new TriggerInterval(interval));
    }

    private void invokeBatchs() {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Properties properties = new Properties();
        long execID = jobOperator.start("collect_messages", properties);
    }

}
