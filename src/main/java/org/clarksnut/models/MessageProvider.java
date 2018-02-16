package org.clarksnut.models;

import java.util.Date;

public interface MessageProvider {

    MessageModel addMessage(String messageId, Date internalDate, BrokerModel broker);

    MessageModel getMessage(String id);

    MessageModel getUserByMessageIdAndBroker(String messageId, BrokerModel broker);
}
