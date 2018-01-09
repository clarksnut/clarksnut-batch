package org.clarksnut.mail;

import org.clarksnut.mail.exceptions.MailReadException;

public interface MailUblMessageModel {

    byte[] getXml() throws MailReadException;

}
