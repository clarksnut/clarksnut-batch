package org.clarksnut.batchs.messages.send;

import jodd.io.ZipBuilder;
import org.clarksnut.models.jpa.entity.FileEntity;
import org.clarksnut.models.jpa.entity.GroupFileEntity;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Named;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

@Named
public class SendGroupFilesProcessor implements ItemProcessor {

    private static final Logger logger = Logger.getLogger(SendGroupFilesProcessor.class);

    @Override
    public Object processItem(Object item) throws Exception {
        Map<GroupFileEntity, GenericEntity> result = new HashMap<>();

        GroupFileEntity groupFileEntity = (GroupFileEntity) item;
        groupFileEntity.setSended(true);

        ZipBuilder zipInMemory = ZipBuilder.createZipInMemory();
        for (FileEntity fileEntity : groupFileEntity.getFiles()) {
            zipInMemory.add(fileEntity.getFile())
                    .path(fileEntity.getFilename())
                    .save();
        }
        byte[] zipFile = zipInMemory.toBytes();

        MultipartFormDataOutput mdo = new MultipartFormDataOutput();
        mdo.addFormData("file", new ByteArrayInputStream(zipFile), MediaType.APPLICATION_OCTET_STREAM_TYPE, "file.zip");
        GenericEntity<MultipartFormDataOutput> genericEntity = new GenericEntity<MultipartFormDataOutput>(mdo) {
        };

        result.put(groupFileEntity, genericEntity);
        return result;
    }
}
