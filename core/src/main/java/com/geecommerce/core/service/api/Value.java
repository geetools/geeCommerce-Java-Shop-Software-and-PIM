package com.geecommerce.core.service.api;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

public interface Value {
    public String getString();

    public Integer getInteger();

    public Long getLong();

    public Double getDouble();

    public Float getFloat();

    public Short getShort();

    public Boolean getBoolean();

    public BigDecimal getBigDecimal();

    public BigInteger getBigInteger();

    public Character getChar();

    public UUID getUUID();

    public String getAscii();

    public Date getDate();

    public Object getRaw();

    public <E extends Enum<E>> E getEnum(Class<E> enumType);
}