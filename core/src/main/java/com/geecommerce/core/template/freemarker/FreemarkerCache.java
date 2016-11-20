package com.geecommerce.core.template.freemarker;

import java.util.concurrent.TimeUnit;

import com.geecommerce.core.cache.ContextAwareCacheKeyWrapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import freemarker.cache.CacheStorage;

public class FreemarkerCache implements CacheStorage {
    private static Cache<Object, Object> CACHE = null;

    private static final Object LOCK = new Object();

    public FreemarkerCache() {
        if (CACHE == null) {
            synchronized (LOCK) {
                if (CACHE == null) {
                    CACHE = CacheBuilder.newBuilder().maximumSize(100000).expireAfterWrite(14, TimeUnit.DAYS).build();
                }
            }
        }
    }

    @Override
    public Object get(Object key) {
        Object value = CACHE.getIfPresent(new ContextAwareCacheKeyWrapper(key));

        // if(value != null)
        // {
        // System.out.println("FREEMARKER FROM CACHE - " +
        // ReflectionToStringBuilder.toString(key));
        // } else {
        // System.out.println("FREEMARKER NOT FROM CACHE - " +
        // ReflectionToStringBuilder.toString(key));
        // }
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        // System.out.println("FREEMARKER PUT TO CACHE - " + key);
        CACHE.put(new ContextAwareCacheKeyWrapper(key), value);
    }

    @Override
    public void remove(Object key) {
        CACHE.invalidate(new ContextAwareCacheKeyWrapper(key));
        // System.out.println("FREEMARKER REMOVED FROM CACHE - " + key);
    }

    @Override
    public void clear() {
        // System.out.println("FREEMARKER CLEAR !!!!!!");

        CACHE.invalidateAll();
        CACHE.cleanUp();
    }

    // @Override
    public boolean isConcurrent() {
        return true;
    }
}
