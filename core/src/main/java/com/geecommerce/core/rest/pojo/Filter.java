package com.geecommerce.core.rest.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.TypeConverter;

public class Filter implements Serializable {
    private static final long serialVersionUID = 3896483860216451097L;

    private List<String> fields = null;
    private List<String> attributes = null;
    private List<String> sort = null;
    private Long offset = null;
    private Integer limit = null;
    private Boolean noCache = null;
    private Map<String, Object> params = new HashMap<>();

    public Filter() {

    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public List<String> getSort() {
        return sort;
    }

    public void setSort(List<String> sort) {
        this.sort = sort;
    }

    public void setSortField(String sortField) {
        if (sort == null) {
            sort = new ArrayList<>();
        }

        sort.clear();
        sort.add(sortField);
    }

    public void addSortField(String sortField) {
        if (sort == null) {
            sort = new ArrayList<>();
        }

        sort.add(sortField);
    }

    public boolean hasSortFields() {
        return sort != null && sort.size() > 0;
    }

    public Long getOffset() {
        return offset;
    }

    public Filter setOffset(Long offset) {
        this.offset = offset;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public Filter setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public boolean isNoCache() {
        return noCache == null ? false : noCache.booleanValue();
    }

    public Filter setNoCache(Boolean noCache) {
        this.noCache = noCache;
        return this;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public Map<String, Object> getParams(Map<String, Class<?>> types) {
        Set<String> keys = params.keySet();

        Map<String, Object> typedParams = new HashMap<>();

        for (String key : keys) {
            Object val = params.get(key);
            Class<?> type = types.get(key);

            if (type != null) {
                typedParams.put(key, TypeConverter.convert(type, val));
            } else {
                typedParams.put(key, val);
            }
        }

        return typedParams;
    }

    public Filter setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public Filter append(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public final Object get(final String key) {
        return params.get(key);
    }

    public final String getString(final String key, final String defaultValue) {
        return contains(key) ? getString(key) : defaultValue;
    }

    public final String getString(final String key) {
        return TypeConverter.asString(get(key));
    }

    public final List<String> getStrings(final String key) {
        return getTypedList(String.class, key);
    }

    public final Short getShort(final String key, final Short defaultValue) {
        return contains(key) ? getShort(key) : defaultValue;
    }

    public final Short getShort(final String key) {
        return TypeConverter.asShort(get(key));
    }

    public final List<Short> getShorts(final String key) {
        return getTypedList(Short.class, key);
    }

    public final Integer getInteger(final String key, final Integer defaultValue) {
        return contains(key) ? getInteger(key) : defaultValue;
    }

    public final Integer getInteger(final String key) {
        return TypeConverter.asInteger(get(key));
    }

    public final List<Integer> getIntegers(final String key) {
        return getTypedList(Integer.class, key);
    }

    public final Long getLong(final String key, final Long defaultValue) {
        return contains(key) ? getLong(key) : defaultValue;
    }

    public final Long getLong(final String key) {
        return TypeConverter.asLong(get(key));
    }

    public final List<Long> getLongs(final String key) {
        return getTypedList(Long.class, key);
    }

    public final Double getDouble(final String key, final Double defaultValue) {
        return contains(key) ? getDouble(key) : defaultValue;
    }

    public final Double getDouble(final String key) {
        return TypeConverter.asDouble(get(key));
    }

    public final List<Double> getDoubles(final String key) {
        return getTypedList(Double.class, key);
    }

    public final Float getFloat(final String key, final Float defaultValue) {
        return contains(key) ? getFloat(key) : defaultValue;
    }

    public final Float getFloat(final String key) {
        return TypeConverter.asFloat(get(key));
    }

    public final List<Float> getFloats(final String key) {
        return getTypedList(Float.class, key);
    }

    public final BigDecimal getBigDecimal(final String key, final BigDecimal defaultValue) {
        return contains(key) ? getBigDecimal(key) : defaultValue;
    }

    public final BigDecimal getBigDecimal(final String key) {
        return TypeConverter.asBigDecimal(get(key));
    }

    public final List<BigDecimal> getBigDecimals(final String key) {
        return getTypedList(BigDecimal.class, key);
    }

    public final BigInteger getBigInteger(final String key, final BigInteger defaultValue) {
        return contains(key) ? getBigInteger(key) : defaultValue;
    }

    public final BigInteger getBigInteger(final String key) {
        return TypeConverter.asBigInteger(get(key));
    }

    public final List<BigInteger> getBigIntegers(final String key) {
        return getTypedList(BigInteger.class, key);
    }

    public final Boolean getBoolean(final String key, final Boolean defaultValue) {
        return contains(key) ? getBoolean(key) : defaultValue;
    }

    public final Boolean getBoolean(final String key) {
        return TypeConverter.asBoolean(get(key));
    }

    public final List<Boolean> getBooleans(final String key) {
        return getTypedList(Boolean.class, key);
    }

    public final Date getDate(final String key) {
        return TypeConverter.asDate(get(key));
    }

    public final List<Date> getDates(final String key) {
        return getTypedList(Date.class, key);
    }

    public final <E extends Enum<E>> E getEnum(final Class<E> enumType, final String key, final E defaultValue) {
        return contains(key) ? getEnum(enumType, key) : defaultValue;
    }

    public final <E extends Enum<E>> E getEnum(final Class<E> enumType, final String key) {
        return TypeConverter.asEnum(enumType, get(key));
    }

    public final <E extends Enum<E>> List<E> getEnums(final Class<E> enumType, final String key) {
        return getTypedList(enumType, key);
    }

    public final Id getId(final String key) {
        return TypeConverter.asId(get(key));
    }

    public final List<Id> getIds(final String key) {
        return getTypedList(Id.class, key);
    }

    public final Id getUUID(final String key) {
        return TypeConverter.asId(get(key));
    }

    public final List<UUID> getUUIDs(final String key) {
        return getTypedList(UUID.class, key);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private final <T> List<T> getTypedList(final Class<T> clazz, final String key) {
        List<T> typedList = new ArrayList<>();

        Object object = params.get(key);

        List<Object> valueList = null;

        if (!(object instanceof List)) {
            valueList = new ArrayList<>();
            valueList.add(object);
        } else {
            valueList = (List<Object>) params.get(key);
        }

        for (Object value : valueList) {
            if (BigDecimal.class == clazz) {
                typedList.add((T) TypeConverter.asBigDecimal(value));
            } else if (BigInteger.class == clazz) {
                typedList.add((T) TypeConverter.asBigInteger(value));
            } else if (Boolean.class == clazz) {
                typedList.add((T) TypeConverter.asBoolean(value));
            } else if (Date.class == clazz) {
                typedList.add((T) TypeConverter.asDate(value));
            } else if (Double.class == clazz) {
                typedList.add((T) TypeConverter.asDouble(value));
            } else if (clazz.isEnum()) {
                typedList.add((T) TypeConverter.asEnum((Class<Enum>) clazz, value));
            } else if (Float.class == clazz) {
                typedList.add((T) TypeConverter.asFloat(value));
            } else if (Id.class == clazz) {
                typedList.add((T) TypeConverter.asId(value));
            } else if (Integer.class == clazz) {
                typedList.add((T) TypeConverter.asInteger(value));
            } else if (Long.class == clazz) {
                typedList.add((T) TypeConverter.asLong(value));
            } else if (Short.class == clazz) {
                typedList.add((T) TypeConverter.asShort(value));
            } else if (String.class == clazz) {
                typedList.add((T) TypeConverter.asString(value));
            } else if (UUID.class == clazz) {
                typedList.add((T) TypeConverter.asUUID(value));
            }
        }

        return typedList;
    }

    public final boolean contains(final String key) {
        return params.containsKey(key);
    }

    @Override
    public String toString() {
        return "Filter [fields=" + fields + ", attributes=" + attributes + ", sort=" + sort + ", offset=" + offset
            + ", limit=" + limit + ", noCache=" + noCache + ", params=" + params + "]";
    }
}
