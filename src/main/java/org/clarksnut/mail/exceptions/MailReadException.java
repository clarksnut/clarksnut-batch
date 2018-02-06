package org.clarksnut.mail.exceptions;

public class MailReadException extends Exception {

    public MailReadException(String message) {
        super(message);
    }

    public MailReadException(String message, Throwable e) {
        super(message, e);
    }

}
