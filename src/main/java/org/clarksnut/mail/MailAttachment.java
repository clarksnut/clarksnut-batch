package org.clarksnut.mail;

public class MailAttachment {

    private final String filename;
    private final String attachmentId;

    public MailAttachment(String filename, String attachmentId) {
        this.filename = filename;
        this.attachmentId = attachmentId;
    }

    public String getFilename() {
        return filename;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

}
