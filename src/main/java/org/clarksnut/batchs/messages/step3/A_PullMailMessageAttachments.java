package org.clarksnut.batchs.messages.step3;

import org.clarksnut.batchs.BatchLogger;
import org.clarksnut.mail.MailProvider;
import org.clarksnut.mail.MailUtils;
import org.clarksnut.models.BrokerType;
import org.clarksnut.models.jpa.entity.AttachmentEntity;
import org.clarksnut.models.jpa.entity.BrokerEntity;
import org.jberet.support.io.JpaItemReaderWriterBase;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemReader;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Named
public class A_PullMailMessageAttachments extends JpaItemReaderWriterBase implements ItemReader {

    /**
     * {@code javax.enterprise.inject.Instance} that holds optional injection of
     * {@code javax.persistence.Query}.
     */
    @Inject
    protected Instance<Query> queryInstance;

    /**
     * a Java Persistence query string. Optional properties, and defaults to null.
     */
    @Inject
    @BatchProperty
    protected String jpqlQuery;

    /**
     * The name of a query defined in JPA metadata.
     * Optional property, and defaults to null.
     */
    @Inject
    @BatchProperty
    protected String namedQuery;

    /**
     * A native SQL query string. Optional property, and defaults to null.
     */
    @Inject
    @BatchProperty
    protected String nativeQuery;

    /**
     * A name of the stored procedure in the database.
     * Optional procedure, and defaults to null.
     */
    @Inject
    @BatchProperty
    protected String storedProcedureQuery;

    /**
     * A name assigned to the stored procedure query in JPA metadata.
     * Optional property, and defaults to null.
     */
    @Inject
    @BatchProperty
    protected String namedStoredProcedureQuery;

    /**
     * The Java type of the query result object.
     * Optional properties, and defaults to null.
     */
    @Inject
    @BatchProperty
    protected Class beanType;

    /**
     * Name of the resultset mapping. Optional properties, and defaults to null.
     * If specified, it is used to create native query or stored procedure query.
     */
    @Inject
    @BatchProperty
    protected String resultSetMapping;

    /**
     * Query hint properties, as a list of key-value pairs separated by comma (,).
     * Optional property, and defaults to null.
     */
    @Inject
    @BatchProperty
    protected Map<String, String> hints;

    /**
     * Position of the first result, numbered from 0. Optional property, and
     * defaults to 0.
     */
    @Inject
    @BatchProperty
    protected int firstResult;

    /**
     * Maximum number of results to retrieve by the query. Optional property, and
     * defaults to 0 (no limit).
     */
    @Inject
    @BatchProperty
    protected int maxResults;

    /**
     * The JPA query object
     */
    protected Query query;

    /**
     * List to hold query result objects
     */
    protected List<AttachmentEntity> resultList;

    /**
     * Current read position
     */
    protected int readPosition;

    /**
     * Map of attachmentId and bytes
     */
    protected Map<String, byte[]> attachmentsMap;


    @Inject
    private MailUtils mailUtils;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        query = getQuery();

        resultList = query.getResultList();
        attachmentsMap = new HashMap<>();

        Map<BrokerEntity, List<AttachmentEntity>> attachmentsGroupedByBroker = resultList
                .stream()
                .collect(Collectors.groupingBy(f -> f.getMessage().getBroker()));

        for (Map.Entry<BrokerEntity, List<AttachmentEntity>> entry : attachmentsGroupedByBroker.entrySet()) {
            BrokerEntity brokerEntity = entry.getKey();
            List<AttachmentEntity> attachmentEntities = entry.getValue();

            BrokerType brokerType = brokerEntity.getType();
            Optional<MailProvider> optionalProvider = mailUtils.getMailReader(brokerType);

            if (optionalProvider.isPresent()) {
                MailProvider provider = optionalProvider.get();

                Map<String, String> attachmentsRequest = attachmentEntities.stream()
                        .collect(Collectors.toMap(AttachmentEntity::getAttachmentId, attachmentEntity -> attachmentEntity.getMessage().getMessageId()));
                Map<String, byte[]> attachmentsResponse = provider
                        .getAttachments(brokerEntity.getEmail(), brokerEntity.getRefreshToken(), attachmentsRequest);

                attachmentsMap.putAll(attachmentsResponse);
            } else {
                BatchLogger.LOGGER.brokerProviderNotFound(brokerType);
            }
        }

        if (checkpoint == null) {
            readPosition = 0;
        } else {
            readPosition = (Integer) checkpoint;
        }
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public Object readItem() throws Exception {
        if (readPosition >= resultList.size()) {
            return null;
        }

        AttachmentEntity attachment = resultList.get(readPosition++);
        byte[] file = attachmentsMap.get(attachment.getAttachmentId());

        return new AbstractMap.SimpleEntry<>(attachment, file);
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return readPosition;
    }

    protected Query getQuery() {
        Query q = null;
        if (queryInstance != null && !queryInstance.isUnsatisfied()) {
            q = queryInstance.get();
        }
        if (q == null) {
            if (jpqlQuery != null) {
                q = beanType != null ? em.createQuery(jpqlQuery, beanType) :
                        em.createQuery(jpqlQuery);
            } else if (namedQuery != null) {
                q = beanType != null ? em.createNamedQuery(namedQuery, beanType) :
                        em.createNamedQuery(namedQuery);
            } else if (nativeQuery != null) {
                q = beanType != null ? em.createNativeQuery(nativeQuery, beanType) :
                        resultSetMapping != null ? em.createNativeQuery(nativeQuery, resultSetMapping) :
                                em.createNativeQuery(nativeQuery);
            } else if (storedProcedureQuery != null) {
                q = beanType != null ? em.createStoredProcedureQuery(storedProcedureQuery, beanType) :
                        resultSetMapping != null ? em.createStoredProcedureQuery(storedProcedureQuery, resultSetMapping) :
                                em.createStoredProcedureQuery(storedProcedureQuery);
            } else if (namedStoredProcedureQuery != null) {
                q = em.createNamedStoredProcedureQuery(namedStoredProcedureQuery);
            }
        }

        if (firstResult != 0) {
            q.setFirstResult(firstResult);
        }
        if (maxResults != 0) {
            q.setMaxResults(maxResults);
        }

        if (hints != null) {
            for (final Map.Entry<String, String> e : hints.entrySet()) {
                q.setHint(e.getKey(), e.getValue());
            }
        }

        return q;
    }

}
