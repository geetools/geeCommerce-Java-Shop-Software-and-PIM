package com.geecommerce.core.system.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.Id;

@XmlRootElement(name = "configuration")
public interface ConfigurationDefinition extends MultiContextModel {
    public Id getId();

    public ConfigurationDefinition setId(Id id);

    static final class Column {
        public static final String ID = "_id";
        public static final String KEY = "key";
        public static final String VALUE = "value";
    }
}
