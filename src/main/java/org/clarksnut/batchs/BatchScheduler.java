package org.clarksnut.batchs;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.Resource;
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

    @Inject
    @ConfigurationValue("clarksnut.document.apiUrl")
    private Optional<String> clarksnutDocumentApiUrl;

    @Resource
    private ManagedScheduledExecutorService scheduler;

    public void init() {
        // By default every 5 minutes
        Integer interval = clarksnutSchedulerPullInterval.orElse(120);

        // Default 5 seconds of delay
        scheduler.scheduleAtFixedRate(this::collectMessages, 5, interval, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::sendMessages, 60, interval, TimeUnit.SECONDS);
    }

    private void collectMessages() {
        BatchRuntime.getJobOperator().start("collect_messages", new Properties());
    }

    private void sendMessages() {
        String apiUrl = clarksnutDocumentApiUrl.orElse("http://localhost:8080/api/documents");
        Properties properties = new Properties();
        properties.put("clarksnutDocumentApiUrl", apiUrl);

        BatchRuntime.getJobOperator().start("send_messages", properties);
    }

}
