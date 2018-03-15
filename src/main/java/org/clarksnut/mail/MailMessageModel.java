package org.clarksnut.mail;

import org.clarksnut.mail.exceptions.MailReadException;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface MailMessageModel {

    String getMessageId();

    List<MailAttachment> getAttachments() throws MailReadException;

    Date getReceiveDate();

}
