package org.clarksnut.models.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.FileModel;
import org.clarksnut.models.MessageModel;
import org.clarksnut.models.MessageModel;
import org.clarksnut.models.jpa.entity.MessageEntity;
import org.clarksnut.models.jpa.entity.MessageEntity;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MessageAdapter implements MessageModel, JpaModel<MessageEntity> {

    private final Session session;
    private final MessageEntity message;

    public MessageAdapter(Session session, MessageEntity message) {
        this.session = session;
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
