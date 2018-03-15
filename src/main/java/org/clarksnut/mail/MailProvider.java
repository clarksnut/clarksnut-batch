package org.clarksnut.mail;

import org.clarksnut.mail.exceptions.MailReadException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface MailProvider {

    List<MailMessageModel> getMessages(String email, String refreshToken, MailQuery query) throws MailReadException;

    /**
     * @param email        mail of broker
     * @param refreshToken refresh token of broker
     * @param attachments  key, value of attachmentId and MessageId <attachmentId, messageId>
     */
    Map<String, byte[]> getAttachments(String email, String refreshToken, Map<String, String> attachments) throws MailReadException;

    boolean validate(String refreshToken) throws IOException;
}
