package org.clarksnut.models;

public interface MessageProvider {

    MessageModel getMessage(String id);

    MessageModel addMessage(String messageId, BrokerModel broker);

    MessageModel getUserByMessageIdAndBroker(String messageId, BrokerModel broker);
}
