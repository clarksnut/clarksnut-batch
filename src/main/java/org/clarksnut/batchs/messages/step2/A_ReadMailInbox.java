package org.clarksnut.batchs.messages.step2;

import org.clarksnut.batchs.BatchLogger;
import org.clarksnut.batchs.core.ClarksnutJpaItemReader;
import org.clarksnut.mail.MailMessageModel;
import org.clarksnut.mail.MailProvider;
import org.clarksnut.mail.MailQuery;
import org.clarksnut.mail.MailUtils;
import org.clarksnut.models.BrokerType;
import org.clarksnut.models.jpa.entity.BrokerEntity;
import org.clarksnut.models.jpa.entity.MessageEntity;
import org.jberet.support.io.JpaItemReader;
import org.jberet.support.io.JpaItemReaderWriterBase;
import org.jboss.logging.Logger;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemReader;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityGraph;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.*;

@Named
public class A_ReadMailInbox extends ClarksnutJpaItemReader implements ItemReader {

    private static final Logger logger = Logger.getLogger(A_ReadMailInbox.class);

    /**
     * Messages by broker
     */
    protected Map<BrokerEntity, List<MailMessageModel>> resultMap;

    @Inject
    private MailUtils mailUtils;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        query = getQuery();

        resultList = query.getResultList();
        resultMap = new HashMap<>();

        for (BrokerEntity brokerEntity : (List<BrokerEntity>) resultList) {
            BrokerType brokerType = brokerEntity.getType();

            MailQuery mailQuery = getMailQuery(brokerEntity);

            Optional<MailProvider> optionalProvider = mailUtils.getMailReader(brokerType);
            if (optionalProvider.isPresent()) {
                MailProvider provider = optionalProvider.get();
                List<MailMessageModel> messages = provider.getMessages(brokerEntity.getEmail(), brokerEntity.getRefreshToken(), mailQuery);
                resultMap.put(brokerEntity, messages);
            } else {
                resultMap.put(brokerEntity, Collections.emptyList());
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

        BrokerEntity brokerEntity = (BrokerEntity) resultList.get(readPosition++);
        return new AbstractMap.SimpleEntry<>(brokerEntity, resultMap.get(brokerEntity));
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return readPosition;
    }

    protected MailQuery getMailQuery(BrokerEntity broker) {
        Date lastTimeSynchronize = null;

        Optional<MessageEntity> lastMessage = getLastMessage(broker);
        if (lastMessage.isPresent()) {
            lastTimeSynchronize = lastMessage.get().getMessageDate();
        }

        MailQuery.Builder builder = new MailQuery.Builder();
        builder.fileType("xml");
        if (lastTimeSynchronize != null) {
            builder.after(lastTimeSynchronize);
        }

        return builder.build();
    }

    protected Optional<MessageEntity> getLastMessage(BrokerEntity broker) {
        TypedQuery<MessageEntity> query = em.createNamedQuery("getAllMessagesByBrokerId", MessageEntity.class);

        EntityGraph<?> entityGraph = em.getEntityGraph("graph.BatchLastMessage");
        query.setHint("javax.persistence.fetchgraph", entityGraph);

        query.setParameter("brokerId", broker.getId());
        query.setMaxResults(1);

        List<MessageEntity> resultList = query.getResultList();
        if (resultList.isEmpty()) return Optional.empty();
        else return Optional.of(resultList.get(0));
    }
}
