package com.geecommerce.core.cache;

import java.util.concurrent.TimeUnit;

import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import com.google.inject.Inject;

public class Ehcache<K, V> implements Cache<K, V> {
    private net.sf.ehcache.Cache ehcache = null;
    private String name = null;
    private boolean enabled = true;

    @Inject
    public Ehcache() {
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

	if (ehcache == null)
	    throw new IllegalStateException("No cache available. Call the build method first.");

	if (key instanceof ContextAwareCacheKeyWrapper) {
	    ehcache.put(new Element((ContextAwareCacheKeyWrapper) key, new CacheEntry<V>(value)));
	} else {
	    ehcache.put(new Element(new ContextAwareCacheKeyWrapper(key), new CacheEntry<V>(value)));
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(K key) {
	if (!isEnabled())
	    return null;

	if (ehcache == null)
	    throw new IllegalStateException("No cache available. Call the build method first.");

	CacheEntry<V> entry = null;

	if (key instanceof ContextAwareCacheKeyWrapper) {
	    Element e = ehcache.get(key);

	    if (e != null)
		entry = ((CacheEntry<V>) e.getObjectValue());
	} else {
	    Element e = ehcache.get(new ContextAwareCacheKeyWrapper(key));

	    if (e != null)
		entry = ((CacheEntry<V>) e.getObjectValue());
	}

	return entry == null ? null : entry.getValue();
    }

    @Override
    public boolean containsKey(K key) {
	if (!isEnabled())
	    return false;

	if (ehcache == null)
	    throw new IllegalStateException("No cache available. Call the build method first.");

	if (key instanceof ContextAwareCacheKeyWrapper) {
	    return ehcache.get(key) != null;
	} else {
	    return ehcache.get(new ContextAwareCacheKeyWrapper(key)) != null;
	}
    }

    @Override
    public void remove(K key) {
	if (!isEnabled())
	    return;

	if (ehcache == null)
	    throw new IllegalStateException("No cache available. Call the build method first.");

	if (key instanceof ContextAwareCacheKeyWrapper) {
	    ehcache.remove(key);
	} else {
	    ehcache.remove(new ContextAwareCacheKeyWrapper(key));
	}
    }

    @Override
    public void emptyCache() {
	if (!isEnabled())
	    return;

	if (ehcache == null)
	    throw new IllegalStateException("No cache available. Call the build method first.");

	ehcache.removeAll();
    }

    @Override
    public long size() {
	if (!isEnabled())
	    return 0;

	if (ehcache == null)
	    throw new IllegalStateException("No cache available. Call the build method first.");

	return ehcache.getSize();
    }

    @SuppressWarnings("unchecked")
    @Override
    public K[] keySet() {
	if (!isEnabled())
	    return null;

	if (ehcache == null)
	    throw new IllegalStateException("No cache available. Call the build method first.");

	if (size() > 0) {
	    return (K[]) ehcache.getKeys().toArray();
	}

	return null;
    }

    @Override
    public void printKeys() {
	if (ehcache == null)
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
    public Cache<K, V> build(String name, long maxSize, long timout, TimeUnit timeUnit) {
	this.name = name;

	net.sf.ehcache.CacheManager cm = net.sf.ehcache.CacheManager.getInstance();

	long time = 600;

	switch (timeUnit) {
	case MILLISECONDS:
	    time = timout / 1000;
	    break;
	case SECONDS:
	    time = timout;
	    break;
	case MINUTES:
	    time = timout * 60;
	    break;
	case HOURS:
	    time = timout * 60 * 60;
	    break;
	case DAYS:
	    time = timout * 60 * 60 * 24;
	    break;
	}

	net.sf.ehcache.Cache c = new net.sf.ehcache.Cache(name, (int) maxSize, false, false, time, 0);

	cm.addCacheIfAbsent(c);

	ehcache = c;

	return this;
    }

    @Override
    public Cache<K, V> build(String name) {
	this.name = name;

	net.sf.ehcache.CacheManager cm = net.sf.ehcache.CacheManager.getInstance();

	net.sf.ehcache.Cache c = new net.sf.ehcache.Cache(name, 10000, false, false, 600, 0);
	CacheConfiguration configuration = c.getCacheConfiguration();
	configuration.setOverflowToDisk(false);

	cm.addCacheIfAbsent(c);

	ehcache = cm.getCache(name);

	return this;
    }
}
