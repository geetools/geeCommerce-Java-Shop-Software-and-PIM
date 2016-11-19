package com.geecommerce.core.system.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.TypeConverter;
import com.google.common.collect.Maps;

@Cacheable(repository = true)
@XmlRootElement(name = "configuration")
@Model(collection = "configuration", readCount = true, optimisticLocking = true)
public class DefaultConfigurationProperty extends AbstractMultiContextModel implements ConfigurationProperty {
    private static final long serialVersionUID = 2012276802129778322L;
    private Id id = null;
    private String key = null;
    private ContextObject<Object> value = null;

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public DefaultConfigurationProperty() {
        super();
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ConfigurationProperty setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public ConfigurationProperty setKey(String key) {
        this.key = key;
        return this;
    }

    @Override
    public ContextObject<Object> getValue() {
        return value;
    }

    @Override
    public ConfigurationProperty setValue(ContextObject<Object> value) {
        this.value = value;
        return this;
    }

    @Override
    public String getStringValue() {
        return value == null ? null : TypeConverter.asString(value.getVal());
    }

    @Override
    public List<String> getStrings() {
        return value == null ? null : TypeConverter.asList(value.getVal());
    }

    @Override
    public Map<String, String> getStringMap() {
        return value == null ? null : TypeConverter.asMap(value.getVal());
    }

    @Override
    public Map<String, Object> getMap() {
        return value == null ? null : TypeConverter.asMap(value.getVal());
    }

    @Override
    public Double getDoubleValue() {
        return value == null ? null : TypeConverter.asDouble(value.getVal());
    }

    @Override
    public Long getLongValue() {
        return value == null ? null : TypeConverter.asLong(value.getVal());
    }

    @Override
    public List<Long> getLongValues() {
        return value == null ? null : TypeConverter.asList(value.getVal());
    }

    @Override
    public Integer getIntegerValue() {
        return value == null ? null : TypeConverter.asInteger(value.getVal());
    }

    @Override
    public List<Integer> getIntegerValues() {
        return value == null ? null : TypeConverter.asList(value.getVal());
    }

    @Override
    public Float getFloatValue() {
        return value == null ? null : TypeConverter.asFloat(value.getVal());
    }

    @Override
    public Boolean getBooleanValue() {
        return value == null ? null : TypeConverter.asBoolean(value.getVal());
    }

    @Override
    public <E extends Enum<E>> E getEnumValue(Class<E> enumType) {
        return value == null ? null : TypeConverter.asEnum(enumType, value.getVal());
    }

    @Override
    public <E extends Enum<E>> List<E> getEnumValues(Class<E> enumType) {
        return value == null ? null : TypeConverter.asListOfEnums(enumType, value.getVal());
    }

    @Override
    public Date getDateValue() {
        return value == null ? null : TypeConverter.asDate(value.getVal());
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null || map.size() == 0)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.key = str_(map.get(Column.KEY));

        Object val = raw_(map.get(Column.VALUE));

        if (val instanceof Collection && ((Collection) val).size() > 0 && Map.class.isAssignableFrom(((Collection) val).iterator().next().getClass())) {
            ContextObject<Object> ctxVal = ctxObj_(val);
            this.value = ctxVal;
        } else {
            this.value = ContextObjects.global(val);
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Column.ID, getId());
        map.put(Column.KEY, getKey());
        map.put(Column.VALUE, getValue());

        return map;
    }
}
