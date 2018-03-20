package org.clarksnut.batchs.messages.step3;

import org.clarksnut.batchs.BatchLogger;
import org.clarksnut.batchs.core.ClarksnutJpaItemReader;
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
public class A_PullMailMessageAttachments extends ClarksnutJpaItemReader implements ItemReader {

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

        Map<BrokerEntity, List<AttachmentEntity>> attachmentsGroupedByBroker = ((List<AttachmentEntity>) resultList)
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

        AttachmentEntity attachment = (AttachmentEntity) resultList.get(readPosition++);
        byte[] file = attachmentsMap.get(attachment.getAttachmentId());

        return new AbstractMap.SimpleEntry<>(attachment, file);
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return readPosition;
    }

}
