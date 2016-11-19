package com.geecommerce.optivobroadmail.mailer.exception;

public class RecipientListException extends MailerServiceException {
    public RecipientListException() {
    }

    public RecipientListException(String message) {
	super(message);
    }

    public RecipientListException(String message, Throwable cause) {
	super(message, cause);
    }

    public RecipientListException(Throwable cause) {
	super(cause);
    }

}
