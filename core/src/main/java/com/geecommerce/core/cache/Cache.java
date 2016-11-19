package com.geecommerce.core.cache;

import java.util.concurrent.TimeUnit;

public interface Cache<K, V> {
    public String name();

    public void name(String name);

    public boolean isEnabled();

    public void enabled(boolean enabled);

    public void put(K key, V value);

    public V get(K key);

    public boolean containsKey(K key);

    public void remove(K key);

    public void emptyCache();

    public long size();

    public K[] keySet();

    public void printKeys();

    public Cache<K, V> build(String name, long maxSize, long timout, TimeUnit timeUnit);

    public Cache<K, V> build(String name);
}
