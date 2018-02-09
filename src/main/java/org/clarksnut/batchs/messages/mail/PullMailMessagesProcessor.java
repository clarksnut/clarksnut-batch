package org.clarksnut.batchs.messages.mail;

import org.clarksnut.mail.*;
import org.clarksnut.models.jpa.entity.FileEntity;
import org.clarksnut.models.jpa.entity.UserLinkedBrokerEntity;
import org.clarksnut.models.utils.XmlValidator;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.util.*;

@Named
public class PullMailMessagesProcessor implements ItemProcessor {

    @Inject
    private MailUtils mailUtils;

    @Override
    public Object processItem(Object item) throws Exception {
        UserLinkedBrokerEntity entity = (UserLinkedBrokerEntity) item;


        Map<UserLinkedBrokerEntity, List<FileEntity>> result = new HashMap<>();
        result.put(entity, new ArrayList<>());


        MailProvider mailProvider = mailUtils.getMailReader(entity.getType());
        if (mailProvider != null) {
            MailRepositoryModel repository = MailRepositoryModel.builder()
                    .email(entity.getEmail())
                    .refreshToken(entity.getUser().getOfflineToken())
                    .build();

            MailQuery.Builder queryBuilder = MailQuery.builder().fileType("xml");
            LocalDateTime lastTimeSynchronized = entity.getLastTimeSynchronized();
            if (lastTimeSynchronized != null) {
                queryBuilder.after(lastTimeSynchronized);
            }

            TreeSet<MailUblMessageModel> messages = mailProvider.getUblMessages(repository, queryBuilder.build());
            for (MailUblMessageModel message : messages) {
                byte[] xml = message.getXml();
                if (XmlValidator.test(xml)) {
                    String id = UUID.randomUUID().toString();

                    FileEntity fileEntity = new FileEntity();
                    fileEntity.setId(id);
                    fileEntity.setFilename(id + ".xml");
                    fileEntity.setFile(xml);

                    result.get(entity).add(fileEntity);
                }
            }

            // Update last sync
            MailUblMessageModel lastMessage = messages.last();
            entity.setLastTimeSynchronized(lastMessage.getReceiveDate());
        }

        return result;
    }

}
