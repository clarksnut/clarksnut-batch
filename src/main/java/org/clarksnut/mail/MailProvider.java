package org.clarksnut.mail;

import org.clarksnut.mail.exceptions.MailReadException;

import java.util.List;

public interface MailProvider {

    List<MailUblMessageModel> getUblMessages(MailRepositoryModel repository, MailQuery query) throws MailReadException;

}
