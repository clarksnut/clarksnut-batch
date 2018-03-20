package org.clarksnut.batchs;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.Resource;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.*;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Singleton
public class BatchScheduler {

    @PersistenceContext
    private EntityManager em;

    @Inject
    @ConfigurationValue("clarksnut.scheduler.pull.interval")
    private Optional<Integer> clarksnutSchedulerPullInterval;

    @Inject
    @ConfigurationValue("clarksnut.document.apiUrl")
    private Optional<String> clarksnutDocumentApiUrl;

    @Resource
    private ManagedScheduledExecutorService scheduler;

    public void initScheduler() {
        // By default every 5 minutes
        Integer interval = clarksnutSchedulerPullInterval.orElse(120);

        // Default 5 seconds of delay
        scheduler.scheduleAtFixedRate(this::collectMessages, 5, interval, TimeUnit.SECONDS);
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void init(String brokerId) {
        Properties properties = getDefaultConfig();

        Query queryToValidateBrokers = em.createNamedQuery("getBrokerById")
                .setParameter("brokerId", brokerId)
                .setHint("javax.persistence.loadgraph", em.getEntityGraph("graph.BatchBroker"));
        properties.put("clarksnutBeforeChunkQuery", queryToValidateBrokers);

        Query queryToPullMessagesFrom = em.createNamedQuery("getBrokerById")
                .setParameter("brokerId", brokerId)
                .setHint("javax.persistence.loadgraph", em.getEntityGraph("graph.BatchBroker"));
        properties.put("clarksnutImportFromMailChunkQuery", queryToPullMessagesFrom);

        Query queryToPullAttachmentsFrom = em.createNamedQuery("batch_getAllAttachmentsFromBrokerWithNoFile").setParameter("brokerId", brokerId);
        properties.put("clarksnutImportAttachmentChunkQuery", queryToPullAttachmentsFrom);

        Query queryToSendFilesTo = em.createNamedQuery("batch_getAllAttachmentsFromBrokerWithFileAndPendingSendStatus").setParameter("brokerId", brokerId);
        properties.put("clarksnutSendFilesChunkQuery", queryToSendFilesTo);

        BatchRuntime.getJobOperator().start("collect_messages", properties);
    }

    private void collectMessages() {
        Properties properties = getDefaultConfig();
        BatchRuntime.getJobOperator().start("collect_messages", properties);
    }

    private Properties getDefaultConfig() {
        String apiUrl = clarksnutDocumentApiUrl.orElse("http://localhost:8080/api/documents");
        Properties properties = new Properties();
        properties.put("clarksnutDocumentApiUrl", apiUrl);
        return properties;
    }

}
