package org.clarksnut.mail;

import org.clarksnut.models.BrokerType;

import javax.enterprise.util.AnnotationLiteral;

public class MailVendorTypeLiteral extends AnnotationLiteral<MailVendorType> implements MailVendorType {

    private final BrokerType value;

    public MailVendorTypeLiteral(BrokerType value) {
        this.value = value;
    }

    @Override
    public BrokerType value() {
        return value;
    }

}
