package com.geecommerce.core.system.model;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface ContextMessage extends MultiContextModel {
    public Id getId();

    public ContextMessage setId(Id id);

    public String getKey();

    public ContextMessage setKey(String key);

    public ContextObject<String> getValue();

    public ContextMessage setValue(ContextObject<String> value);

    public String getMessage();

    static final class Column {
        public static final String ID = "_id";
        public static final String KEY = "key";
        public static final String VALUE = "value";
    }
}
