package org.clarksnut.mail;

import org.clarksnut.models.BrokerType;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Optional;

@Stateless
public class MailUtils {

    private static final Logger logger = Logger.getLogger(MailUtils.class);

    @Inject
    @Any
    private Instance<MailProvider> providers;

    public Optional<MailProvider> getMailReader(BrokerType brokerType) {
        Annotation annotation = new MailVendorTypeLiteral(brokerType);
        Instance<MailProvider> instance = providers.select(annotation);
        if (instance.isAmbiguous() || instance.isUnsatisfied()) {
            return Optional.empty();
        }
        return Optional.of(instance.get());
    }

}