package com.geecommerce.core.cache;

import java.io.Serializable;

public class CacheEntry<T> implements Serializable {
    private static final long serialVersionUID = -5256640200505518186L;
    private final T value;

    public CacheEntry(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
