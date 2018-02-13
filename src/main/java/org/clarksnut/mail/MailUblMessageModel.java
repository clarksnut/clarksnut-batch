package org.clarksnut.mail;

import org.clarksnut.mail.exceptions.MailReadException;

import java.util.Date;
import java.util.Set;

public interface MailUblMessageModel {

    String getMessageId();

    Set<MailAttachment> getXmlFiles() throws MailReadException;

    Date getReceiveDate();

}
