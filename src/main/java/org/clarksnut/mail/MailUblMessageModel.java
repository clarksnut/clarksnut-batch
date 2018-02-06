package org.clarksnut.mail;

import org.clarksnut.mail.exceptions.MailReadException;

import java.time.LocalDateTime;

public interface MailUblMessageModel {

    byte[] getXml() throws MailReadException;

    LocalDateTime getReceiveDate();

}
