package com.geecommerce.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.geecommerce.core.cache.ContextAwareCacheKeyWrapper;
import com.geecommerce.core.type.Nullable;

public class NullableConcurrentHashMap<K, V> implements Map<K, V> {
    private Map<ContextAwareCacheKeyWrapper<K>, Nullable<V>> map = new ConcurrentHashMap<>();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean containsKey(Object key) {
        if (key instanceof ContextAwareCacheKeyWrapper) {
            return map.containsKey(key);
        } else {
            return map.containsKey(new ContextAwareCacheKeyWrapper(key));
        }
    }

    @Override
    public boolean containsValue(Object value) {
        if (value instanceof Nullable) {
            return map.containsValue(value);
        } else {
            return map.containsValue(Nullable.wrap(value));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public V get(Object key) {
        if (key instanceof ContextAwareCacheKeyWrapper) {
            return Nullable.unwrap(map.get(key));
        } else {
            return Nullable.unwrap(map.get(new ContextAwareCacheKeyWrapper(key)));
        }
    }

    @Override
    public V put(K key, V value) {
        return putIfAbsent(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V putIfAbsent(K key, V value) {
        if (key instanceof ContextAwareCacheKeyWrapper) {
            return Nullable.unwrap(map.putIfAbsent((ContextAwareCacheKeyWrapper<K>) key, Nullable.wrap(value)));
        } else {
            return Nullable.unwrap(map.putIfAbsent(new ContextAwareCacheKeyWrapper<K>(key), Nullable.wrap(value)));
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public V remove(Object key) {
        if (key instanceof ContextAwareCacheKeyWrapper) {
            return Nullable.unwrap(map.remove((ContextAwareCacheKeyWrapper) key));
        } else {
            return Nullable.unwrap(map.remove(new ContextAwareCacheKeyWrapper(key)));
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new IllegalStateException("putAll() not implemented in this map");
    }

    @Override
    public void clear() {
        map.clear();

    }

    @Override
    public Set<K> keySet() {
        Set<ContextAwareCacheKeyWrapper<K>> keys = map.keySet();
        Set<K> keySet = new HashSet<>();

        for (ContextAwareCacheKeyWrapper<K> key : keys) {
            if (key.isInContext()) {
                keySet.add(key.getKey());
            }
        }

        return keySet;
    }

    @Override
    public Collection<V> values() {
        Set<ContextAwareCacheKeyWrapper<K>> keys = map.keySet();
        Collection<V> values = new ArrayList<>();

        for (ContextAwareCacheKeyWrapper<K> key : keys) {
            if (key.isInContext()) {
                values.add(Nullable.unwrap(map.get(key)));
            }
        }

        return values;
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        throw new IllegalStateException("entrySet() not implemented in this map");
    }
}
