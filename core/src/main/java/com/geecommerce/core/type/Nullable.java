package com.geecommerce.core.type;

import java.io.Serializable;

public class Nullable<T> implements Serializable {
    private static final long serialVersionUID = -5677533311025550852L;

    private Object value = null;

    public Nullable(T object) {
	this.value = object;
    }

    @SuppressWarnings("unchecked")
    public <T> T get() {
	return value == null ? null : (T) value;

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Nullable<T> wrap(Object object) {
	return new Nullable(object);
    }

    @SuppressWarnings("unchecked")
    public static <T> T unwrap(Nullable<T> nullable) {
	return nullable == null ? null : (T) nullable.value;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((value == null) ? 0 : value.hashCode());
	return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Nullable other = (Nullable) obj;
	if (value == null) {
	    if (other.value != null)
		return false;
	} else if (!value.equals(other.value))
	    return false;
	return true;
    }
}
