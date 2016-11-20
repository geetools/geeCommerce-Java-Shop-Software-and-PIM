package com.geecommerce.webmessage.model;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model
public class DefaultWebMessage extends AbstractModel implements WebMessage {

    private String code = null;
    private String status = null;
    private String message = null;

    @Override
    public WebMessage setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public WebMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public WebMessage setStatus(String status) {
        this.status = status;
        return this;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public Id getId() {
        return null;
    }
}
