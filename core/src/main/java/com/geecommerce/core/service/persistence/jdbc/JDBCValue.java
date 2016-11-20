package com.geecommerce.core.service.persistence.jdbc;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import com.geecommerce.core.service.api.Value;
import com.geecommerce.core.type.TypeConverter;

public class JDBCValue implements Value {
    Object object = null;

    @SuppressWarnings("unused")
    private JDBCValue() {

    }

    public JDBCValue(Object object) {
        this.object = object;
    }

    @Override
    public String getString() {
        if (isNull(object))
            return null;

        if (object instanceof String) {
            return (String) object;
        } else if (object instanceof byte[]) {
            try {
                return new String((byte[]) object, "utf-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return String.valueOf(object);
        }
    }

    @Override
    public Integer getInteger() {
        if (isNull(object))
            return null;

        if (object instanceof Number) {
            return ((Number) object).intValue();
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName()
                + " to " + Integer.class.getName());
        }
    }

    @Override
    public Long getLong() {
        if (isNull(object))
            return null;

        if (object instanceof Number) {
            return ((Number) object).longValue();
        } else {
            throw new IllegalArgumentException(
                "Unable to convert object of type " + object.getClass().getName() + " to " + Long.class.getName());
        }
    }

    @Override
    public Double getDouble() {
        if (isNull(object))
            return null;

        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName()
                + " to " + Double.class.getName());
        }
    }

    @Override
    public Float getFloat() {
        if (isNull(object))
            return null;

        if (object instanceof Number) {
            return ((Number) object).floatValue();
        } else {
            throw new IllegalArgumentException(
                "Unable to convert object of type " + object.getClass().getName() + " to " + Float.class.getName());
        }
    }

    @Override
    public Short getShort() {
        if (isNull(object))
            return null;

        if (object instanceof Number) {
            return ((Number) object).shortValue();
        } else {
            throw new IllegalArgumentException(
                "Unable to convert object of type " + object.getClass().getName() + " to " + Short.class.getName());
        }
    }

    @Override
    public Boolean getBoolean() {
        if (isNull(object))
            return null;

        if (object instanceof Boolean) {
            return (Boolean) object;
        } else if (object instanceof Number) {
            int n = ((Number) object).intValue();

            if (n != 0 && n != 1) {
                throw new IllegalArgumentException("If boolean is stored as an int then it must be '0' or '1'");
            }

            return n == 1 ? true : false;
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName()
                + " to " + Boolean.class.getName());
        }
    }

    @Override
    public BigDecimal getBigDecimal() {
        if (isNull(object))
            return null;

        if (object instanceof Number) {
            return new BigDecimal(((Number) object).doubleValue());
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName()
                + " to " + BigDecimal.class.getName());
        }
    }

    @Override
    public BigInteger getBigInteger() {
        if (isNull(object))
            return null;

        if (object instanceof Number) {
            return new BigInteger(String.valueOf((Number) object));
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName()
                + " to " + BigInteger.class.getName());
        }
    }

    @Override
    public Character getChar() {
        if (isNull(object))
            return null;

        if (object instanceof Character) {
            return (Character) object;
        } else if (object instanceof Number) {
            int n = ((Number) object).intValue();

            return (char) n;
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName()
                + " to " + Character.class.getName());
        }
    }

    @Override
    public <E extends Enum<E>> E getEnum(Class<E> enumType) {
        if (isNull(object))
            return null;

        return TypeConverter.asEnum(enumType, object);
    }

    @Override
    public UUID getUUID() {
        if (isNull(object))
            return null;

        if (object instanceof Value) {
            return ((Value) object).getUUID();
        } else if (object instanceof String) {
            return UUID.fromString((String) object);
        } else {
            throw new IllegalArgumentException(
                "Unable to convert object of type " + object.getClass().getName() + " to " + UUID.class.getName());
        }
    }

    @Override
    public String getAscii() {
        return (String) object;
    }

    @Override
    public Date getDate() {
        if (object instanceof Timestamp) {
            return new Date(((java.sql.Timestamp) object).getTime());
        } else {
            return new Date(((java.sql.Date) object).getTime());
        }
    }

    @Override
    public Object getRaw() {
        return object;
    }

    protected boolean isNull(Object object) {
        if (object == null)
            return true;

        return false;
    }
}
