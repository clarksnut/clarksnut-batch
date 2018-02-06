package org.clarksnut.batchs;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.Resource;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class BatchScheduler {

    @Inject
    @ConfigurationValue("clarksnut.scheduler.pull.interval")
    private Optional<Integer> clarksnutSchedulerPullInterval;

    @Resource
    private ManagedScheduledExecutorService scheduler;

    public void init() {
        // By default every 5 minutes
        Integer interval = clarksnutSchedulerPullInterval.orElse(60);

        // Default 5 seconds of delay
        scheduler.scheduleAtFixedRate(this::invokeBatchs, 5, interval, TimeUnit.SECONDS);
    }

    private void invokeBatchs() {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Properties properties = new Properties();
        long execID = jobOperator.start("collect_messages", properties);
    }

}
