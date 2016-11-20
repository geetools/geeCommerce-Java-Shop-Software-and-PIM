package com.geecommerce.core.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.geecommerce.core.App;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultCacheManager implements CacheManager {
    private static final long serialVersionUID = -2336974124600762227L;

    protected static final Object LOCK = new Object();

    protected final ConcurrentMap<String, Cache<?, ?>> caches = new ConcurrentHashMap<String, Cache<?, ?>>();

    protected static final String PROPERTY_KEY_SUFFIX_MAXSIZE = ".maxsize";
    protected static final String PROPERTY_KEY_SUFFIX_TIMEOUT = ".timeout";
    protected static final String PROPERTY_KEY_SUFFIX_ENABLED = ".enabled";

    protected static final Map<String, TimeUnit> timeUnits = new HashMap<String, TimeUnit>();
    static {
        timeUnits.put("m", TimeUnit.MILLISECONDS);
        timeUnits.put("s", TimeUnit.SECONDS);
        timeUnits.put("M", TimeUnit.MINUTES);
        timeUnits.put("h", TimeUnit.HOURS);
        timeUnits.put("d", TimeUnit.DAYS);
    }

    @Inject
    protected App app;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <K, V> Cache<K, V> getCache(String name) {
        Cache cache = caches.get(name);

        if (cache == null) {
            synchronized (LOCK) {
                cache = caches.get(name);

                if (cache == null)
                    cache = addCache(name);
            }
        }

        return cache;
    }

    private synchronized Cache<?, ?> addCache(String name) {
        String maxSize = CacheProps.GET.val(new StringBuilder(name).append(PROPERTY_KEY_SUFFIX_MAXSIZE).toString());
        String timeout = CacheProps.GET.val(new StringBuilder(name).append(PROPERTY_KEY_SUFFIX_TIMEOUT).toString());
        String enabled = CacheProps.GET.val(new StringBuilder(name).append(PROPERTY_KEY_SUFFIX_ENABLED).toString());

        boolean isEnabled = true;

        if (enabled != null) {
            isEnabled = Boolean.valueOf(enabled.trim());
        }

        Cache<?, ?> c = null;

        if (maxSize != null && timeout != null) {
            String t = timeout.trim();
            String time = t.length() == 1 ? t : t.substring(0, t.length() - 1);
            String unit = t.length() == 1 ? null : t.substring(t.length() - 1);

            // System.out.println(name + " ---> [enabled=" + isEnabled + ",
            // maxSize=" + Long.valueOf(maxSize) +
            // ", time=" + Long.valueOf(time) + ", unit=" + timeUnits.get(unit)
            // + "]");

            c = app.inject(Cache.class).build(name, Long.valueOf(maxSize), Long.valueOf(time),
                unit == null ? null : timeUnits.get(unit));
        } else {
            c = app.inject(Cache.class).build(name);
        }

        c.enabled(isEnabled);

        caches.putIfAbsent(name, c);

        return c;
    }

    @Override
    public void invalidateWhereKeyLike(String cacheNameRegex, String keyRegex) {
        Set<String> cacheNames = getCacheNames();

        for (String cacheName : cacheNames) {
            // We only want to invalidate query-caches.
            if (!cacheName.matches(cacheNameRegex))
                continue;

            Cache<Object, List<Id>> queryCache = getCache(cacheName);

            if (queryCache != null) {
                Object[] queryCacheKeys = queryCache.keySet();

                if (queryCacheKeys != null && queryCacheKeys.length > 0) {
                    List<Object> keysToRemove = new ArrayList<>();

                    for (Object key : queryCacheKeys) {
                        List<Id> ids = queryCache.get(key);

                        if (key instanceof ContextAwareCacheKeyWrapper) {
                            ContextAwareCacheKeyWrapper keyw = (ContextAwareCacheKeyWrapper) key;
                            Object k = keyw.getKey();

                            if (k instanceof String && ((String) k).matches(keyRegex)) {
                                // System.out.println("CHECKING STR KEY TO
                                // REMOVE ::: " + key);
                                // System.out.println("-----> " + ids);
                                keysToRemove.add(key);
                            }
                        }
                    }

                    // System.out.println("REMOVING ::::::::::::::::::::::: " +
                    // keysToRemove);

                    for (Object queryToRemove : keysToRemove) {
                        queryCache.remove(queryToRemove);
                    }
                }
            }
        }
    }

    @Override
    public void emptyCaches() {
        Set<String> cacheNames = getCacheNames();

        for (String cacheName : cacheNames) {
            Cache<Object, List<Id>> cache = getCache(cacheName);

            if (cache != null) {
                cache.emptyCache();
            }
        }
    }

    @Override
    public Set<String> getCacheNames() {
        return caches.keySet();
    }
}
