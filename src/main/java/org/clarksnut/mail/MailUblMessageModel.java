package org.clarksnut.mail;

import org.clarksnut.mail.exceptions.MailReadException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface MailUblMessageModel {

    String getMessageId();

    Set<MailAttachment> getXmlFiles() throws MailReadException;

    Date getReceiveDate();

}
