package com.geecommerce.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AppRegistry {
    private static ThreadLocal<Map<String, Object>> REGISTRY_THREAD_LOCAL = new ThreadLocal<Map<String, Object>>() {
        protected Map<String, Object> initialValue() {
            return new HashMap<String, Object>();
        }
    };

    public static final void put(String key, Object value) {
        registryMap().put(key, value);
    }

    public static final void putAll(Map<String, Object> map) {
        registryMap().putAll(map);
    }

    @SuppressWarnings("unchecked")
    public static final <T> T get(String key) {
        return (T) registryMap().get(key);
    }

    @SuppressWarnings("unchecked")
    public static final <T> T get(String key, T defaultValue) {
        T val = (T) registryMap().get(key);

        return val == null ? defaultValue : val;
    }

    public static final Map<String, Object> getAll() {
        return registryMap();
    }

    public static final void remove(String key) {
        registryMap().remove(key);
    }

    public static final void clear() {
        registryMap().clear();
    }

    public static final Set<String> keySet() {
        return registryMap().keySet();
    }

    public static void cleanupThread() {
        clear();
        REGISTRY_THREAD_LOCAL.remove();
    }

    protected static final Map<String, Object> registryMap() {
        return REGISTRY_THREAD_LOCAL.get();
    }
}
