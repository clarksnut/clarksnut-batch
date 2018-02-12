package org.clarksnut.mail.gmail;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import org.clarksnut.mail.MailAttachment;
import org.clarksnut.mail.MailRepositoryModel;
import org.clarksnut.mail.MailUblMessageModel;
import org.clarksnut.mail.exceptions.MailReadException;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class GmailMessageAdapter implements MailUblMessageModel {

    private final MailRepositoryModel mailRepository;
    private final Gmail client;
    private final Message message;

    public GmailMessageAdapter(Gmail gmail, MailRepositoryModel mailRepository, Message message) {
        this.mailRepository = mailRepository;
        this.client = gmail;
        this.message = message;
    }

    @Override
    public String getMessageId() {
        return message.getId();
    }

    @Override
    public Set<MailAttachment> getXmlFiles() throws MailReadException {
        try {
            return getFileByExtension(".xml", ".XML");
        } catch (IOException e) {
            throw new MailReadException("Could not retrieve xml document from gmail broker", e);
        }
    }

    @Override
    public Date getReceiveDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(message.getInternalDate());
        return cal.getTime();
    }

    private Set<MailAttachment> getFileByExtension(String... validExtension) throws IOException {
        if (validExtension == null || validExtension.length == 0) {
            throw new IllegalStateException("Invalid extension");
        }

        Set<MailAttachment> result = new HashSet<>();

        List<MessagePart> parts = message.getPayload().getParts();
        for (MessagePart part : parts) {
            String filename = part.getFilename();
            if (filename != null && filename.length() > 0) {
                boolean endsWith = false;
                for (String extension : validExtension) {
                    if (filename.endsWith(extension)) {
                        endsWith = true;
                        break;
                    }
                }

                if (endsWith) {
                    String attachmentId = part.getBody().getAttachmentId();
                    MessagePartBody messagePartBody = client.users()
                            .messages()
                            .attachments()
                            .get(mailRepository.getEmail(), message.getId(), attachmentId)
                            .execute();

                    Base64 base64url = new Base64(true);
                    byte[] bytes = base64url.decodeBase64(messagePartBody.getData());

                    result.add(new MailAttachment(bytes, part.getFilename()));
                }
            }
        }

        return result;
    }
}
