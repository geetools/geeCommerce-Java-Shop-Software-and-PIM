package com.geecommerce.core.cache;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;

public class DefaultCache<K, V> implements Cache<K, V> {
    private com.google.common.cache.Cache<ContextAwareCacheKeyWrapper<K>, Object> googleCache;
    private Map<ContextAwareCacheKeyWrapper<K>, Object> mapCache;

    private String name = null;
    private boolean enabled = true;

    @Inject
    public DefaultCache() {
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void name(String name) {
        this.name = name;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void put(K key, V value) {
        if (!isEnabled())
            return;

        if (googleCache == null && mapCache == null)
            throw new IllegalStateException("No cache available. Call the build method first.");

        if (key instanceof ContextAwareCacheKeyWrapper) {
            if (googleCache != null) {
                googleCache.put((ContextAwareCacheKeyWrapper) key, new CacheEntry<V>(value));
            } else {
                mapCache.put((ContextAwareCacheKeyWrapper) key, new CacheEntry<V>(value));
            }
        } else {
            if (googleCache != null) {
                googleCache.put(new ContextAwareCacheKeyWrapper(key), new CacheEntry<V>(value));
            } else {
                mapCache.put(new ContextAwareCacheKeyWrapper(key), new CacheEntry<V>(value));
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(K key) {
        if (!isEnabled())
            return null;

        if (googleCache == null && mapCache == null)
            throw new IllegalStateException("No cache available. Call the build method first.");

        CacheEntry<V> entry = null;

        if (key instanceof ContextAwareCacheKeyWrapper) {
            if (googleCache != null) {
                entry = ((CacheEntry<V>) googleCache.getIfPresent(key));
            } else {
                entry = ((CacheEntry<V>) mapCache.get(key));
            }
        } else {
            if (googleCache != null) {
                entry = ((CacheEntry<V>) googleCache.getIfPresent(new ContextAwareCacheKeyWrapper(key)));
            } else {
                entry = ((CacheEntry<V>) mapCache.get(new ContextAwareCacheKeyWrapper(key)));
            }
        }

        return entry == null ? null : entry.getValue();
    }

    @Override
    public boolean containsKey(K key) {
        if (!isEnabled())
            return false;

        if (googleCache == null && mapCache == null)
            throw new IllegalStateException("No cache available. Call the build method first.");

        if (key instanceof ContextAwareCacheKeyWrapper) {
            if (googleCache != null) {
                return googleCache.asMap().containsKey(key);
            } else {
                return mapCache.containsKey(key);
            }

        } else {
            if (googleCache != null) {
                return googleCache.asMap().containsKey(new ContextAwareCacheKeyWrapper(key));
            } else {
                return mapCache.containsKey(new ContextAwareCacheKeyWrapper(key));
            }
        }
    }

    @Override
    public void remove(K key) {
        if (!isEnabled())
            return;

        if (googleCache == null && mapCache == null)
            throw new IllegalStateException("No cache available. Call the build method first.");

        if (key instanceof ContextAwareCacheKeyWrapper) {
            if (googleCache != null) {
                googleCache.invalidate(key);
            } else {
                mapCache.remove(key);
            }

        } else {
            if (googleCache != null) {
                googleCache.invalidate(new ContextAwareCacheKeyWrapper(key));
            } else {
                mapCache.remove(new ContextAwareCacheKeyWrapper(key));
            }
        }
    }

    @Override
    public void emptyCache() {
        if (!isEnabled())
            return;

        if (googleCache == null && mapCache == null)
            throw new IllegalStateException("No cache available. Call the build method first.");

        if (googleCache != null) {
            googleCache.invalidateAll();
        } else {
            mapCache.clear();
        }
    }

    @Override
    public long size() {
        if (!isEnabled())
            return 0;

        if (googleCache == null && mapCache == null)
            throw new IllegalStateException("No cache available. Call the build method first.");

        if (googleCache != null) {
            return googleCache.size();
        } else {
            return mapCache.size();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public K[] keySet() {
        if (!isEnabled())
            return null;

        if (googleCache == null && mapCache == null)
            throw new IllegalStateException("No cache available. Call the build method first.");

        Class<?> keyType = null;
        Set<K> keySet = new HashSet<>();

        if (googleCache != null) {
            Set<ContextAwareCacheKeyWrapper<K>> keys = googleCache.asMap().keySet();

            for (ContextAwareCacheKeyWrapper<K> key : keys) {
                if (key.isInContext()) {
                    if (keyType == null)
                        keyType = key.getKey().getClass();

                    keySet.add(key.getKey());
                }
            }

            return keySet.size() == 0 ? (K[]) keySet.toArray() : (K[]) Array.newInstance(keyType, keySet.size());
        } else {
            Set<ContextAwareCacheKeyWrapper<K>> keys = mapCache.keySet();

            for (ContextAwareCacheKeyWrapper<K> key : keys) {
                if (key.isInContext()) {
                    if (keyType == null)
                        keyType = key.getKey().getClass();

                    keySet.add(key.getKey());
                }
            }

            return keySet.size() == 0 ? (K[]) keySet.toArray() : (K[]) Array.newInstance(keyType, keySet.size());
        }
    }

    @Override
    public void printKeys() {
        if (googleCache == null && mapCache == null)
            throw new IllegalStateException("No cache available. Call the build method first.");

        StringBuffer out = new StringBuffer();
        out.append("------------------------------------------------------\n");
        out.append("Cache output\n");
        out.append("------------------------------------------------------\n");

        if (!isEnabled()) {
            out.append("Cache '" + name + "' is not enabled\n");
        }

        Object[] keys = keySet();

        if (keys != null && keys.length > 0) {
            int x = 0;
            for (Object k : keys) {
                out.append((++x) + ": " + ((ContextAwareCacheKeyWrapper) k).getKey().toString()).append("\n");
            }
        } else {
            out.append("No cache values to output.\n");
        }

        System.out.println(out.toString());
    }

    @Override
    public Cache<K, V> build(String name, long maxSize, long timeout, TimeUnit timeUnit) {
        this.name = name;

        if (maxSize == 0 && timeout == 0) {
            mapCache = new ConcurrentHashMap<>();
        } else {
            googleCache = CacheBuilder.newBuilder().maximumSize(maxSize).expireAfterWrite(timeout, timeUnit).build();
        }

        return this;
    }

    @Override
    public Cache<K, V> build(String name) {
        this.name = name;

        googleCache = CacheBuilder.newBuilder().maximumSize(100000).expireAfterWrite(10, TimeUnit.MINUTES).build();

        return this;
    }
}
