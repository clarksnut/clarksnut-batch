package org.clarksnut.models;

import java.util.List;

public interface MessageModel {

    String getId();
    String getProvider();
    String getMessageId();

    List<FileModel> getAttachments();
}
