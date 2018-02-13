package org.clarksnut.mail;

import org.clarksnut.mail.exceptions.MailReadException;

import java.util.TreeSet;

public interface MailProvider {

    TreeSet<MailUblMessageModel> getUblMessages(MailRepositoryModel repository, MailQuery query) throws MailReadException;

}
