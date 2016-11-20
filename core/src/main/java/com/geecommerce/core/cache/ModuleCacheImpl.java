package com.geecommerce.core.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.geemodule.api.Module;
import com.geemodule.api.ModuleCache;
import com.google.common.collect.Sets;

public class ModuleCacheImpl implements ModuleCache {
    private final Map<ModuleCacheKeyWrapper<String>, Module> moduleCache = new ConcurrentHashMap<>();

    @Override
    public Module putIfAbsent(String key, Module module) {
        if (module == null)
            return null;

        return moduleCache.putIfAbsent(new ModuleCacheKeyWrapper<String>(key), module);
    }

    @Override
    public Module get(String key) {
        return moduleCache.get(new ModuleCacheKeyWrapper<String>(key));
    }

    @Override
    public boolean containsKey(String key) {
        return moduleCache.containsKey(new ModuleCacheKeyWrapper<String>(key));
    }

    @Override
    public void remove(String key) {
        moduleCache.remove(new ModuleCacheKeyWrapper<String>(key));
    }

    @Override
    public Set<String> keySet() {
        Set<ModuleCacheKeyWrapper<String>> keys = moduleCache.keySet();
        Set<String> keySet = Sets.newHashSet();

        for (ModuleCacheKeyWrapper<String> moduleCacheKeyWrapper : keys) {
            keySet.add(String.valueOf(moduleCacheKeyWrapper.getKey()));
        }

        return keySet;
    }

    @Override
    public Collection<Module> getAll() {
        return moduleCache.values();
    }

    @Override
    public int size() {
        return moduleCache.size();
    }
}
