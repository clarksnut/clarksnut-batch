package org.clarksnut.batchs.messages.mail;

import org.clarksnut.models.jpa.entity.BrokerEntity;
import org.clarksnut.models.jpa.entity.FileEntity;
import org.clarksnut.models.jpa.entity.MessageEntity;
import org.jberet.support.io.JpaItemWriter;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

@Named
public class PullMailMessagesWriter extends JpaItemWriter {

    @Override
    public void writeItems(List<Object> items) throws Exception {
        if (entityTransaction) {
            em.getTransaction().begin();
        }

        for (final Object item : items) {
            if (item != null) {
                ((Map<BrokerEntity, List<MessageEntity>>) item).forEach((brokerEntity, messageEntities) -> {
                    em.merge(brokerEntity);
                    for (MessageEntity messageEntity : messageEntities) {
                        TypedQuery<MessageEntity> query = em.createNamedQuery("getMessageByMessageIdAndBrokerId", MessageEntity.class);
                        query.setParameter("brokerId", brokerEntity.getId());
                        query.setParameter("messageId", messageEntity.getMessageId());
                        List<MessageEntity> entities = query.getResultList();
                        if (entities.isEmpty()) {
                            em.persist(messageEntity);

                            for (FileEntity fileEntity : messageEntity.getAttachments()) {
                                fileEntity.setMessage(messageEntity);
                                em.persist(fileEntity);
                            }
                        }
                    }
                });
            }
        }

        if (entityTransaction) {
            em.getTransaction().commit();
        }
    }
}
