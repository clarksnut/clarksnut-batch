package org.clarksnut.models.jpa;

import org.clarksnut.models.FileModel;
import org.clarksnut.models.MessageModel;
import org.clarksnut.models.jpa.entity.MessageEntity;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

public class MessageAdapter implements MessageModel {

    private final EntityManager em;
    private final MessageEntity message;

    public MessageAdapter(EntityManager em, MessageEntity message) {
        this.em = em;
        this.message = message;
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
    public List<FileModel> getAttachments() {
        return message.getFiles().stream()
                .map(f -> new FileAdapter(em, f))
                .collect(Collectors.toList());
    }

}
