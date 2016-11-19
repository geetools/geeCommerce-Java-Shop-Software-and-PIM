package com.geecommerce.core.cache;

public class Keys {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> ContextAwareCacheKeyWrapper<T> create(T key) {
	return new ContextAwareCacheKeyWrapper(key);
    }
}
