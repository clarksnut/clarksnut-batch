package org.clarksnut.models;

public interface MessageProvider {

    MessageModel getMessage(String id);

    MessageModel addMessage(String messageId, String provider);

    MessageModel getUserByMessageIdAndProvider(String messageId, String provider);
}
