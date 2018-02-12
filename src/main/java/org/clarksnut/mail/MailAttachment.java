package org.clarksnut.mail;

public class MailAttachment {

    private final byte[] bytes;
    private final String filename;

    public MailAttachment(byte[] bytes, String filename) {
        this.bytes = bytes;
        this.filename = filename;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MailAttachment that = (MailAttachment) o;

        return filename.equals(that.filename);
    }

    @Override
    public int hashCode() {
        return filename.hashCode();
    }
}
