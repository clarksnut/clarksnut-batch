package org.clarksnut.batchs.messages.step4;

import org.clarksnut.models.jpa.entity.AttachmentEntity;
import org.clarksnut.models.jpa.entity.FileEntity;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Named;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.util.AbstractMap;

@Named
public class A_PrepareFileAttachmentsToSend implements ItemProcessor {

    @Override
    public Object processItem(Object item) throws Exception {
        AttachmentEntity attachmentEntity = (AttachmentEntity) item;
        FileEntity file = attachmentEntity.getFile();

        MultipartFormDataOutput mdo = new MultipartFormDataOutput();
        mdo.addFormData("file", new ByteArrayInputStream(file.getFile()), MediaType.APPLICATION_XML_TYPE, attachmentEntity.getFilename());
        GenericEntity<MultipartFormDataOutput> genericEntity = new GenericEntity<MultipartFormDataOutput>(mdo) {
        };

        return new AbstractMap.SimpleEntry<>(attachmentEntity, genericEntity);
    }

}
