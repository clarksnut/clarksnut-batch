package org.clarksnut.mail.gmail;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import org.clarksnut.mail.MailAttachment;
import org.clarksnut.mail.MailMessageModel;
import org.clarksnut.mail.exceptions.MailReadException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GmailMessageAdapter implements MailMessageModel {

    private final String email;
    private final Gmail client;
    private final Message message;

    public GmailMessageAdapter(Gmail gmail, String email, Message message) {
        this.email = email;
        this.client = gmail;
        this.message = message;
    }

    @Override
    public String getMessageId() {
        return message.getId();
    }

    @Override
    public List<MailAttachment> getAttachments() throws MailReadException {
        List<MailAttachment> result = new ArrayList<>();

        List<MessagePart> parts = message.getPayload().getParts();
        for (MessagePart part : parts) {
            String filename = part.getFilename();
            if (filename != null && filename.length() > 0) {
                String attachmentId = part.getBody().getAttachmentId();
                MailAttachment attachment = new MailAttachment(filename, attachmentId);
                result.add(attachment);
            }
        }

        return result;
    }

    @Override
    public Date getReceiveDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(message.getInternalDate());
        return cal.getTime();
    }

}
