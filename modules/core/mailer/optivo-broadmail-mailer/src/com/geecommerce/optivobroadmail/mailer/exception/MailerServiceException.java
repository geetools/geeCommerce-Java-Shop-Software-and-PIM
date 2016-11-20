package com.geecommerce.optivobroadmail.mailer.exception;

public class MailerServiceException extends Exception {
    public MailerServiceException() {
    }

    public MailerServiceException(String message) {
        super(message);
    }

    public MailerServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailerServiceException(Throwable cause) {
        super(cause);
    }

}
