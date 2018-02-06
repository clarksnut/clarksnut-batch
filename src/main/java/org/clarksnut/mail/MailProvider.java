package org.clarksnut.mail;

import org.clarksnut.mail.exceptions.MailReadException;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;

public interface MailProvider {

    TreeSet<MailUblMessageModel> getUblMessages(MailRepositoryModel repository, MailQuery query) throws MailReadException;

}
