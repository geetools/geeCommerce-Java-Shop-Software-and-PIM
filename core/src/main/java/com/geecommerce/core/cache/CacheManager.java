package com.geecommerce.core.cache;

import java.util.Set;

import com.geecommerce.core.service.api.Injectable;

public interface CacheManager extends Injectable {
    public <K, V> Cache<K, V> getCache(String name);

    public Set<String> getCacheNames();

    public void invalidateWhereKeyLike(String cacheNameRegex, String keyRegex);

    public void emptyCaches();
}
