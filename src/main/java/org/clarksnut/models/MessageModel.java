package org.clarksnut.models;

import java.util.List;

public interface MessageModel {

    String getId();
    String getMessageId();

    List<FileModel> getAttachments();
}
