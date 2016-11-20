package com.geecommerce.core.service;

public class DaoException extends RuntimeException {
    private static final long serialVersionUID = 70642822235495885L;

    public DaoException() {
        super();
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoException(String message) {
        super(message);
    }

    public DaoException(Throwable cause) {
        super(cause);
    }
}
