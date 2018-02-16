package org.clarksnut.models;

import java.util.Date;
import java.util.List;

public interface MessageModel {

    String getId();
    String getMessageId();

    Date getInternalDate();

    List<FileModel> getAttachments();
}
