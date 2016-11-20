package com.geecommerce.webmessage.model;

import com.geecommerce.core.service.api.Model;

public interface WebMessage extends Model {

    public WebMessage setCode(String code);

    public String getCode();

    public WebMessage setMessage(String message);

    public String getMessage();

    public WebMessage setStatus(String status);

    public String getStatus();

    static final class Column {
        public static final String MODULE = "module";
        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
    }
}
