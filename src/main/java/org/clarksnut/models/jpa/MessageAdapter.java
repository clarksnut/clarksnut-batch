package org.clarksnut.models.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.MessageModel;
import org.clarksnut.models.jpa.entity.MessageEntity;

import javax.persistence.EntityManager;
import java.util.Date;

public class MessageAdapter implements MessageModel, JpaModel<MessageEntity> {

    private final EntityManager em;
    private final MessageEntity message;

    public MessageAdapter(EntityManager em, MessageEntity message) {
        this.em = em;
        this.message = message;
    }

    public static MessageEntity toEntity(MessageModel model, EntityManager em) {
        if (model instanceof FileAdapter) {
            return ((MessageAdapter) model).getEntity();
        }
        return em.getReference(MessageEntity.class, model.getId());
    }

    @Override
    public MessageEntity getEntity() {
        return message;
    }

    @Override
    public String getId() {
        return message.getId();
    }

    @Override
    public String getMessageId() {
        return message.getMessageId();
    }

    @Override
    public Date getMessageDate() {
        return message.getMessageDate();
    }

}
