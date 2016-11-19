package com.geecommerce.core.service.persistence.mongodb;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.api.Value;
import com.geecommerce.core.type.TypeConverter;

@XmlRootElement()
public final class MongoValue implements Value {
    Object object = null;

    public MongoValue(Object object) {
        this.object = object;
    }

    @Override
    public String getString() {
        return TypeConverter.asString(object);
    }

    @Override
    public Integer getInteger() {
        return TypeConverter.asInteger(object);
    }

    @Override
    public Long getLong() {
        return TypeConverter.asLong(object);
    }

    @Override
    public Double getDouble() {
        return TypeConverter.asDouble(object);
    }

    @Override
    public Float getFloat() {
        return TypeConverter.asFloat(object);
    }

    @Override
    public Short getShort() {
        return TypeConverter.asShort(object);
    }

    @Override
    public Boolean getBoolean() {
        return TypeConverter.asBoolean(object);
    }

    @Override
    public BigDecimal getBigDecimal() {
        return TypeConverter.asBigDecimal(object);
    }

    @Override
    public BigInteger getBigInteger() {
        return TypeConverter.asBigInteger(object);
    }

    @Override
    public Character getChar() {
        return TypeConverter.asCharacter(object);
    }

    @Override
    public <E extends Enum<E>> E getEnum(Class<E> enumType) {
        return TypeConverter.asEnum(enumType, object);
    }

    @Override
    public UUID getUUID() {
        return TypeConverter.asUUID(object);
    }

    @Override
    public String getAscii() {
        return TypeConverter.asASCII(object);
    }

    @Override
    public Date getDate() {
        return TypeConverter.asDate(object);
    }

    @Override
    public Object getRaw() {
        return TypeConverter.asRaw(object);
    }

    @Override
    public String toString() {
        return String.valueOf(object);
    }
}
