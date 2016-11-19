package com.geecommerce.core.type;

import java.io.Serializable;
import java.math.BigInteger;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.geecommerce.core.App;
import com.geecommerce.core.rest.jersey.adapter.IdAdapter;

@XmlJavaTypeAdapter(IdAdapter.class)
public class Id implements Serializable {
    private static final long serialVersionUID = 4375017095333676278L;

    private Long id = null;

    public Id() {

    }

    public Id(Number id) {
        this.id = id.longValue();
    }

    public Id(Long id) {
        this.id = id;
    }

    public Id(Integer id) {
        this.id = id.longValue();
    }

    public Id(BigInteger id) {
        this.id = id.longValue();
    }

    public Id(String id) {
        this.id = Long.parseLong(id);
    }

    public double doubleValue() {
        return id.doubleValue();
    }

    public float floatValue() {
        return id.floatValue();
    }

    public int intValue() {
        return id.intValue();
    }

    public long longValue() {
        return id.longValue();
    }

    public byte byteValue() {
        return id.byteValue();
    }

    public short shortValue() {
        return id.shortValue();
    }

    public Number num() {
        return longValue();
    }

    public String str() {
        return "" + num();
    }

    /**
     * Convenience method for freemarker templates.
     * 
     * @return Number
     */
    public Number getN() {
        return num();
    }

    /**
     * Convenience method for freemarker templates.
     * 
     * @return String
     */
    public String getS() {
        return str();
    }

    public static Id newId() {
        return App.get().nextId();
    }

    public static Id parseId(String id) {
        return new Id(id);
    }

    public static Id[] toIds(Object[] ids) {
        if (ids == null || ids.length == 0)
            return null;

        Id[] resultIds = new Id[ids.length];

        for (int i = 0; i < ids.length; i++) {
            resultIds[i] = toId(ids[i]);
        }

        return resultIds;
    }

    public static Id[] toIds(String ids) {
        return toIds(ids, ",");
    }

    public static Id[] toIds(String ids, String separator) {
        return toIds(ids.split(separator));
    }

    public static Id toId(Object id) {
        if (id == null)
            return null;

        if (id instanceof Id) {
            return (Id) id;
        } else if (id instanceof Number) {
            return new Id((Number) id);
        } else if (id instanceof String) {
            return new Id(((String) id).trim());
        } else {
            throw new IllegalArgumentException("Cannot create Id object from type: " + id.getClass().getName());
        }
    }

    public static Number toNumber(Id id) {
        return id == null ? null : id.num();
    }

    public static Id valueOf(Object id) {
        return toId(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof Id))
            return false;

        return id.equals(((Id) obj).id);
    }

    @Override
    public int hashCode() {
        return id == null ? null : id.hashCode();
    }

    @Override
    public String toString() {
        return id == null ? null : String.valueOf(id);
    }
}
