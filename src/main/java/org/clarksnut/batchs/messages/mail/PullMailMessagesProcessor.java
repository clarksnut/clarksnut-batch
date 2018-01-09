package org.clarksnut.batchs.messages.mail;

import org.clarksnut.mail.*;
import org.clarksnut.models.XmlValidator;
import org.clarksnut.models.jpa.entity.FileEntity;
import org.clarksnut.models.jpa.entity.UserLinkedBrokerEntity;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Named
public class PullMailMessagesProcessor implements ItemProcessor {

    @Inject
    private MailUtils mailUtils;

    @Inject
    private XmlValidator xmlValidator;

    @Override
    public Object processItem(Object item) throws Exception {
        List<FileEntity> result = new ArrayList<>();

        UserLinkedBrokerEntity entity = (UserLinkedBrokerEntity) item;
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

            List<MailUblMessageModel> messages = mailProvider.getUblMessages(repository, queryBuilder.build());
            for (MailUblMessageModel message : messages) {
                byte[] xml = message.getXml();
                if (xmlValidator.test(xml)) {
                    FileEntity fileEntity = new FileEntity();
                    fileEntity.setId(UUID.randomUUID().toString());
                    fileEntity.setFile(xml);

                    result.add(fileEntity);
                }
            }
        }

        return result;
    }

}
