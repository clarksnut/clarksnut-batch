package org.clarksnut.batchs.messages.step2;

import org.clarksnut.batchs.BatchLogger;
import org.clarksnut.mail.MailAttachment;
import org.clarksnut.mail.MailMessageModel;
import org.clarksnut.models.SendStatus;
import org.clarksnut.models.jpa.entity.AttachmentEntity;
import org.clarksnut.models.jpa.entity.BrokerEntity;
import org.clarksnut.models.jpa.entity.MessageEntity;
import org.clarksnut.models.jpa.entity.SendStatusEntity;
import org.jberet.support.io.JpaItemReaderWriterBase;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Named;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

@Named
public class B_ValidateMails extends JpaItemReaderWriterBase implements ItemProcessor {

    @Override
    public Object processItem(Object item) throws Exception {
        List<MessageEntity> result = new ArrayList<>();


        AbstractMap.SimpleEntry<BrokerEntity, List<MailMessageModel>> entry = (AbstractMap.SimpleEntry<BrokerEntity, List<MailMessageModel>>) item;
        BrokerEntity brokerEntity = entry.getKey();
        List<MailMessageModel> mailMessages = entry.getValue();

        for (MailMessageModel mailMessage : mailMessages) {
            Optional<MessageEntity> previousMessage = getMessage(brokerEntity, mailMessage.getMessageId());
            if (previousMessage.isPresent()) {
                BatchLogger.LOGGER.mailMessageWasAlreadyImported(brokerEntity.getType(), brokerEntity.getEmail(), mailMessage.getMessageId());
                continue;
            }

            List<MailAttachment> attachments = filterValidAttachments(mailMessage.getAttachments());
            if (!attachments.isEmpty()) {
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setId(UUID.randomUUID().toString());
                messageEntity.setMessageId(mailMessage.getMessageId());
                messageEntity.setMessageDate(mailMessage.getReceiveDate());
                messageEntity.setBroker(brokerEntity);

                Set<AttachmentEntity> attachmentEntities = attachments.stream()
                        .map(attachment -> {
                            AttachmentEntity attachmentEntity = new AttachmentEntity();
                            attachmentEntity.setId(UUID.randomUUID().toString());
                            attachmentEntity.setAttachmentId(attachment.getAttachmentId());
                            attachmentEntity.setFilename(attachment.getFilename());
                            attachmentEntity.setMessage(messageEntity);

                            SendStatusEntity sendStatusEntity = new SendStatusEntity();
                            sendStatusEntity.setId(UUID.randomUUID().toString());
                            sendStatusEntity.setStatus(SendStatus.WAITING_FOR_SEND);
                            sendStatusEntity.setAttachment(attachmentEntity);

                            attachmentEntity.setSendStatus(sendStatusEntity);
                            return attachmentEntity;
                        })
                        .collect(Collectors.toSet());
                messageEntity.setAttachments(attachmentEntities);

                result.add(messageEntity);
            }
        }

        return result;
    }

    protected List<MailAttachment> filterValidAttachments(List<MailAttachment> attachments) {
        return attachments.stream()
                .filter(p -> p.getFilename().toLowerCase().endsWith(".xml"))
                .collect(Collectors.toList());
    }

    protected Optional<MessageEntity> getMessage(BrokerEntity broker, String messageId) {
        TypedQuery<MessageEntity> query = em.createNamedQuery("getMessageByMessageIdAndBrokerId", MessageEntity.class);
        query.setParameter("messageId", messageId);
        query.setParameter("brokerId", broker.getId());
        List<MessageEntity> entities = query.getResultList();
        if (entities.size() == 0) return Optional.empty();
        else return Optional.of(entities.get(0));
    }
}
