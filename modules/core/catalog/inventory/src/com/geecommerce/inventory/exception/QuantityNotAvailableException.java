package com.geecommerce.inventory.exception;

public class QuantityNotAvailableException extends Exception {
    private static final long serialVersionUID = 9104830957060896184L;

    public QuantityNotAvailableException() {
    }

    public QuantityNotAvailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
    }

    public QuantityNotAvailableException(String message, Throwable cause) {
	super(message, cause);
    }

    public QuantityNotAvailableException(String message) {
	super(message);
    }

    public QuantityNotAvailableException(Throwable cause) {
	super(cause);
    }
}
