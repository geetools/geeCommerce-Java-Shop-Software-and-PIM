package com.geecommerce.core.service.exception;

public class IllegalMultiContextException extends RuntimeException {
    private static final long serialVersionUID = -1816683341049953251L;

    public IllegalMultiContextException() {
	super();
    }

    public IllegalMultiContextException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
    }

    public IllegalMultiContextException(String message, Throwable cause) {
	super(message, cause);
    }

    public IllegalMultiContextException(String message) {
	super(message);
    }

    public IllegalMultiContextException(Throwable cause) {
	super(cause);
    }
}
