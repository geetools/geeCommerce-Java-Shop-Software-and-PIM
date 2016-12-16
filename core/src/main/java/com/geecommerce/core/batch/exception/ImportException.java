package com.geecommerce.core.batch.exception;

public class ImportException extends RuntimeException {
    private static final long serialVersionUID = 6775398416675583881L;

    protected String message = null;
    protected Object[] args = null;

    public ImportException(String message, Object... args) {
        this.message = message;
        this.args = args;
    }

    public String getMessage() {
        return message;
    }

    public Object[] getArgs() {
        return args;
    }
}
