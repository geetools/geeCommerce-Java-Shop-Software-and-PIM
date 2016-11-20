package com.geecommerce.core.system.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.owlike.genson.annotation.JsonIgnore;

@XmlRootElement(name = "configuration")
public interface ConfigurationProperty extends MultiContextModel {
    public Id getId();

    public ConfigurationProperty setId(Id id);

    public String getKey();

    public ConfigurationProperty setKey(String key);

    public ContextObject<Object> getValue();

    public ConfigurationProperty setValue(ContextObject<Object> value);

    public String getStringValue();

    public List<String> getStrings();

    public Map<String, String> getStringMap();

    public Map<String, Object> getMap();

    public Double getDoubleValue();

    public Long getLongValue();

    public List<Long> getLongValues();

    public Integer getIntegerValue();

    public List<Integer> getIntegerValues();

    @JsonIgnore
    public Float getFloatValue();

    public Boolean getBooleanValue();

    public <E extends Enum<E>> E getEnumValue(Class<E> enumType);

    public <E extends Enum<E>> List<E> getEnumValues(Class<E> enumType);

    public Date getDateValue();

    static final class Column {
        public static final String ID = "_id";
        public static final String KEY = "key";
        public static final String VALUE = "value";
    }
}
