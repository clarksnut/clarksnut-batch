package org.clarksnut.mail;

import org.clarksnut.models.BrokerType;
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.lang.annotation.Annotation;

@RequestScoped
public class MailUtils {

    private static final Logger logger = Logger.getLogger(MailUtils.class);

    @Inject
    @Any
    private Instance<MailProvider> providers;

    public MailProvider getMailReader(BrokerType brokerType) {
        Annotation annotation = new MailVendorTypeLiteral(brokerType);
        Instance<MailProvider> instance = providers.select(annotation);
        if (instance.isAmbiguous() || instance.isUnsatisfied()) {
            logger.warn("Could not find a provider for:" + brokerType);
            return null;
        }
        return instance.get();
    }

}