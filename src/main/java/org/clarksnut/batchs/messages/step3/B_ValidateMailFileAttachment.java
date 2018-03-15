package org.clarksnut.batchs.messages.step3;

import org.clarksnut.batchs.BatchLogger;
import org.clarksnut.models.jpa.entity.AttachmentEntity;
import org.clarksnut.models.jpa.entity.BrokerEntity;
import org.clarksnut.models.jpa.entity.FileEntity;
import org.clarksnut.models.jpa.entity.MessageEntity;
import org.clarksnut.models.utils.XmlValidator;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.AbstractMap;
import java.util.UUID;

@Named
public class B_ValidateMailFileAttachment implements ItemProcessor {

    @Inject
    private XmlValidator validator;

    @Override
    public Object processItem(Object item) throws Exception {
        AbstractMap.SimpleEntry<AttachmentEntity, byte[]> entry = (AbstractMap.SimpleEntry<AttachmentEntity, byte[]>) item;
        AttachmentEntity attachmentEntity = entry.getKey();
        byte[] bytes = entry.getValue();

        boolean isUBLFile = validator.isUBLFile(bytes);
        if (isUBLFile) {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setId(UUID.randomUUID().toString());
            fileEntity.setFile(bytes);
            fileEntity.setAttachment(attachmentEntity);

            return fileEntity;
        } else {
            MessageEntity messageEntity = attachmentEntity.getMessage();
            BrokerEntity brokerEntity = messageEntity.getBroker();
            BatchLogger.LOGGER.notValidUBLFile(brokerEntity.getType(), brokerEntity.getEmail(), attachmentEntity.getFilename());
        }

        return null;
    }

}
