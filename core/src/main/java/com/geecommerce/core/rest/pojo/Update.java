package com.geecommerce.core.rest.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.geecommerce.core.rest.jersey.adapter.UpdateAdapter;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.TypeConverter;

@XmlRootElement(name = "update")
@XmlAccessorType(XmlAccessType.FIELD)
public class Update implements Serializable {
    private static final long serialVersionUID = 3943794814223044989L;

    private Id id = null;

    @XmlJavaTypeAdapter(UpdateAdapter.class)
    private UpdateMap fields;
    private UpdateMap vars;
    private Map<String, ContextObject<?>> attributes = null;
    private Map<String, List<Id>> options = null;
    private Map<String, ContextObject<List<Id>>> xOptions = null;
    private Map<String, ContextObject<Boolean>> optOuts = null;

    private List<Id> merchantIds = null;
    private List<Id> storeIds = null;
    private List<Id> requestContextIds = null;

    private boolean saveAsNewCopy = false;

    public Update() {
        fields = new UpdateMap();
    }

    public Update(Map<String, Object> fields) {
        this.fields = new UpdateMap();
        this.fields.putAll(fields);
    }

    public Update(Map<String, Object> fields, Map<String, ContextObject<?>> attributes) {
        this.fields = new UpdateMap();
        this.fields.putAll(fields);
        this.attributes = attributes;
    }

    public Update(Id id, Map<String, Object> fields, Map<String, ContextObject<?>> attributes) {
        this.id = id;
        this.fields = new UpdateMap();
        this.fields.putAll(fields);
        this.attributes = attributes;
    }

    public Update(Id id, Map<String, Object> fields, Map<String, ContextObject<?>> attributes, Map<String, List<Id>> options) {
        this.id = id;
        this.fields = new UpdateMap();
        this.fields.putAll(fields);
        this.attributes = attributes;
        this.options = options;
    }

    public Update(Id id, Map<String, Object> fields, Map<String, ContextObject<?>> attributes, Map<String, List<Id>> options, Map<String, ContextObject<List<Id>>> xOptions) {
        this.id = id;
        this.fields = new UpdateMap();
        this.fields.putAll(fields);
        this.attributes = attributes;
        this.options = options;
        this.xOptions = xOptions;
    }

    public Update(Id id, Map<String, Object> fields, Map<String, ContextObject<?>> attributes, Map<String, List<Id>> options, Map<String, ContextObject<List<Id>>> xOptions,
            Map<String, ContextObject<Boolean>> optOuts) {
        this.id = id;
        this.fields = new UpdateMap();
        this.fields.putAll(fields);
        this.attributes = attributes;
        this.options = options;
        this.xOptions = xOptions;
        this.optOuts = optOuts;
    }

    public Update(Id id, Map<String, Object> fields, Map<String, ContextObject<?>> attributes, Map<String, List<Id>> options, Map<String, ContextObject<List<Id>>> xOptions,
            Map<String, ContextObject<Boolean>> optOuts, List<Id> merchantIds, List<Id> storeIds, List<Id> requestContextIds) {
        this.id = id;
        this.fields = new UpdateMap();
        this.fields.putAll(fields);
        this.attributes = attributes;
        this.options = options;
        this.xOptions = xOptions;
        this.optOuts = optOuts;
        this.merchantIds = merchantIds;
        this.storeIds = storeIds;
        this.requestContextIds = requestContextIds;
    }

    public Update(Id id, Map<String, Object> fields, Map<String, ContextObject<?>> attributes, Map<String, List<Id>> options, Map<String, ContextObject<List<Id>>> xOptions,
            Map<String, ContextObject<Boolean>> optOuts, List<Id> merchantIds, List<Id> storeIds, List<Id> requestContextIds, Boolean saveAsNewCopy) {
        this.id = id;
        this.fields = new UpdateMap();
        this.fields.putAll(fields);
        this.attributes = attributes;
        this.options = options;
        this.xOptions = xOptions;
        this.optOuts = optOuts;
        this.merchantIds = merchantIds;
        this.storeIds = storeIds;
        this.requestContextIds = requestContextIds;
        this.saveAsNewCopy = saveAsNewCopy == null ? false : saveAsNewCopy;
    }

    public Update(Id id, Map<String, Object> fields, Map<String, Object> vars, Map<String, ContextObject<?>> attributes, Map<String, List<Id>> options, Map<String, ContextObject<List<Id>>> xOptions,
            Map<String, ContextObject<Boolean>> optOuts, List<Id> merchantIds, List<Id> storeIds, List<Id> requestContextIds, Boolean saveAsNewCopy) {
        this.id = id;
        this.fields = new UpdateMap();
        this.fields.putAll(fields);
        this.vars = new UpdateMap();
        this.vars.putAll(vars);
        this.attributes = attributes;
        this.options = options;
        this.xOptions = xOptions;
        this.optOuts = optOuts;
        this.merchantIds = merchantIds;
        this.storeIds = storeIds;
        this.requestContextIds = requestContextIds;
        this.saveAsNewCopy = saveAsNewCopy == null ? false : saveAsNewCopy;
    }

    public Id getId() {
        return id;
    }

    public final UpdateMap getFields() {
        return fields;
    }

    public Map<String, ContextObject<?>> getAttributes() {
        return attributes;
    }

    public Map<String, List<Id>> getOptions() {
        return options;
    }

    public Map<String, ContextObject<List<Id>>> getXOptions() {
        return xOptions;
    }

    public final UpdateMap getVars() {
        return vars;
    }

    public Map<String, ContextObject<Boolean>> getOptOuts() {
        return optOuts;
    }

    public List<Id> getMerchantIds() {
        return merchantIds;
    }

    public List<Id> getStoreIds() {
        return storeIds;
    }

    public List<Id> getRequestContextIds() {
        return requestContextIds;
    }

    public boolean isSaveAsNewCopy() {
        return saveAsNewCopy;
    }

    public final Object get(final String name) {
        return fields.get(name);
    }

    public final String asString(final String name, final String defaultValue) {
        return hasField(name) ? asString(name) : defaultValue;
    }

    public final String asString(final String name) {
        return TypeConverter.asString(get(name));
    }

    public final Short asShort(final String name, final Short defaultValue) {
        return hasField(name) ? asShort(name) : defaultValue;
    }

    public final Short asShort(final String name) {
        return TypeConverter.asShort(get(name));
    }

    public final Integer asInteger(final String name, final Integer defaultValue) {
        return hasField(name) ? asInteger(name) : defaultValue;
    }

    public final Integer asInteger(final String name) {
        return TypeConverter.asInteger(get(name));
    }

    public final Long asLong(final String name, final Long defaultValue) {
        return hasField(name) ? asLong(name) : defaultValue;
    }

    public final Long asLong(final String name) {
        return TypeConverter.asLong(get(name));
    }

    public final Double asDouble(final String name, final Double defaultValue) {
        return hasField(name) ? asDouble(name) : defaultValue;
    }

    public final Double asDouble(final String name) {
        return TypeConverter.asDouble(get(name));
    }

    public final Float asFloat(final String name, final Float defaultValue) {
        return hasField(name) ? asFloat(name) : defaultValue;
    }

    public final Float asFloat(final String name) {
        return TypeConverter.asFloat(get(name));
    }

    public final BigDecimal asBigDecimal(final String name, final BigDecimal defaultValue) {
        return hasField(name) ? asBigDecimal(name) : defaultValue;
    }

    public final BigDecimal asBigDecimal(final String name) {
        return TypeConverter.asBigDecimal(get(name));
    }

    public final BigInteger asBigInteger(final String name, final BigInteger defaultValue) {
        return hasField(name) ? asBigInteger(name) : defaultValue;
    }

    public final BigInteger asBigInteger(final String name) {
        return TypeConverter.asBigInteger(get(name));
    }

    public final Boolean asBoolean(final String name, final Boolean defaultValue) {
        return hasField(name) ? asBoolean(name) : defaultValue;
    }

    public final Boolean asBoolean(final String name) {
        return TypeConverter.asBoolean(get(name));
    }

    public final Date asDate(final String name) {
        return TypeConverter.asDate(get(name));
    }

    public final <E extends Enum<E>> E asEnum(final Class<E> enumType, final String name, final E defaultValue) {
        return hasField(name) ? asEnum(enumType, name) : defaultValue;
    }

    public final <E extends Enum<E>> E asEnum(final Class<E> enumType, final String name) {
        return TypeConverter.asEnum(enumType, get(name));
    }

    public final Id asId(final String name) {
        return TypeConverter.asId(get(name));
    }

    public final Id asUUID(final String name) {
        return TypeConverter.asId(get(name));
    }

    public final Object asRaw(final String name) {
        return get(name);
    }

    public final boolean hasField(final String name) {
        return fields.containsKey(name);
    }

    public final Object getVar(final String name) {
        return vars.get(name);
    }

    public final String varAsString(final String name, final String defaultValue) {
        return hasVar(name) ? varAsString(name) : defaultValue;
    }

    public final String varAsString(final String name) {
        return TypeConverter.asString(getVar(name));
    }

    public final Short varAsShort(final String name, final Short defaultValue) {
        return hasVar(name) ? varAsShort(name) : defaultValue;
    }

    public final Short varAsShort(final String name) {
        return TypeConverter.asShort(getVar(name));
    }

    public final Integer varAsInteger(final String name, final Integer defaultValue) {
        return hasVar(name) ? varAsInteger(name) : defaultValue;
    }

    public final Integer varAsInteger(final String name) {
        return TypeConverter.asInteger(getVar(name));
    }

    public final Long varAsLong(final String name, final Long defaultValue) {
        return hasVar(name) ? varAsLong(name) : defaultValue;
    }

    public final Long varAsLong(final String name) {
        return TypeConverter.asLong(getVar(name));
    }

    public final Double varAsDouble(final String name, final Double defaultValue) {
        return hasVar(name) ? varAsDouble(name) : defaultValue;
    }

    public final Double varAsDouble(final String name) {
        return TypeConverter.asDouble(getVar(name));
    }

    public final Float varAsFloat(final String name, final Float defaultValue) {
        return hasVar(name) ? varAsFloat(name) : defaultValue;
    }

    public final Float varAsFloat(final String name) {
        return TypeConverter.asFloat(getVar(name));
    }

    public final BigDecimal varAsBigDecimal(final String name, final BigDecimal defaultValue) {
        return hasVar(name) ? varAsBigDecimal(name) : defaultValue;
    }

    public final BigDecimal varAsBigDecimal(final String name) {
        return TypeConverter.asBigDecimal(getVar(name));
    }

    public final BigInteger varAsBigInteger(final String name, final BigInteger defaultValue) {
        return hasVar(name) ? varAsBigInteger(name) : defaultValue;
    }

    public final BigInteger varAsBigInteger(final String name) {
        return TypeConverter.asBigInteger(getVar(name));
    }

    public final Boolean varAsBoolean(final String name, final Boolean defaultValue) {
        return hasVar(name) ? varAsBoolean(name) : defaultValue;
    }

    public final Boolean varAsBoolean(final String name) {
        return TypeConverter.asBoolean(getVar(name));
    }

    public final Date varAsDate(final String name) {
        return TypeConverter.asDate(getVar(name));
    }

    public final <E extends Enum<E>> E varAsEnum(final Class<E> enumType, final String name, final E defaultValue) {
        return hasVar(name) ? varAsEnum(enumType, name) : defaultValue;
    }

    public final <E extends Enum<E>> E varAsEnum(final Class<E> enumType, final String name) {
        return TypeConverter.asEnum(enumType, getVar(name));
    }

    public final Id varAsId(final String name) {
        return TypeConverter.asId(getVar(name));
    }

    public final Id varAsUUID(final String name) {
        return TypeConverter.asId(getVar(name));
    }

    public final Object varAsRaw(final String name) {
        return getVar(name);
    }

    public final boolean hasVar(final String name) {
        return vars.containsKey(name);
    }

    public final boolean isOptOut(final String name) {
        ContextObject<Boolean> optOut = optOuts.get(name);

        return optOut != null && optOut.getBoolean() != null && optOut.getBoolean() == true;
    }

    public final boolean isEmpty() {
        return fields == null || fields.size() == 0;
    }

    public final boolean varsExist() {
        return vars == null || vars.size() == 0;
    }

    public Map<String, ContextObject<List<Id>>> getxOptions() {
        return xOptions;
    }

    public void setxOptions(Map<String, ContextObject<List<Id>>> xOptions) {
        this.xOptions = xOptions;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public void setFields(UpdateMap fields) {
        this.fields = fields;
    }

    public void setAttributes(Map<String, ContextObject<?>> attributes) {
        this.attributes = attributes;
    }

    public void setOptions(Map<String, List<Id>> options) {
        this.options = options;
    }

    public void setVars(UpdateMap vars) {
        this.vars = vars;
    }

    public void setOptOuts(Map<String, ContextObject<Boolean>> optOuts) {
        this.optOuts = optOuts;
    }

    @Override
    public String toString() {
        return "Update [id=" + id + ", fields=" + fields + ", vars=" + vars + ", attributes=" + attributes + ", options=" + options + ", xOptions=" + xOptions + ", optOuts=" + optOuts
                + ", merchantIds=" + merchantIds + ", storeIds=" + storeIds + ", requestContextIds=" + requestContextIds + ", saveAsNewCopy=" + saveAsNewCopy + "]";
    }

    public static final class UpdateMap extends HashMap<String, Object> {
        private static final long serialVersionUID = 4478431564115327551L;

        public UpdateMap() {
        }

        public UpdateMap(Map<String, Object> map) {
            this.putAll(map);
        }
    }
}
