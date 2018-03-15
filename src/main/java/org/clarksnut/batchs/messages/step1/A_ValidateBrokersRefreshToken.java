package org.clarksnut.batchs.messages.step1;

import org.clarksnut.batchs.BatchLogger;
import org.clarksnut.mail.MailProvider;
import org.clarksnut.mail.MailUtils;
import org.clarksnut.models.BrokerType;
import org.clarksnut.models.jpa.entity.BrokerEntity;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Optional;

@Named
public class A_ValidateBrokersRefreshToken implements ItemProcessor {

    @Inject
    private MailUtils mailUtils;

    @Override
    public Object processItem(Object item) throws Exception {
        BrokerEntity brokerEntity = (BrokerEntity) item;
        BrokerType brokerType = brokerEntity.getType();

        Optional<MailProvider> optionalProvider = mailUtils.getMailReader(brokerType);
        if (optionalProvider.isPresent()) {
            MailProvider provider = optionalProvider.get();

            try {
                boolean result = provider.validate(brokerEntity.getRefreshToken());
                if (!result) {
                    brokerEntity.setEnable(false);
                    BatchLogger.LOGGER.brokerMarkedForDisabling(brokerEntity.getType(), brokerEntity.getEmail());
                }
            } catch (IOException e) {
                BatchLogger.LOGGER.couldNotVerifyRefreshToken(brokerEntity.getType(), brokerEntity.getEmail());
            }
        } else {
            BatchLogger.LOGGER.brokerProviderNotFound(brokerType);
        }

        return brokerEntity;
    }

}
