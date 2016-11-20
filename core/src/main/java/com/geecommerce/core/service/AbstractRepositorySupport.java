package com.geecommerce.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.IdSupport;
import com.geecommerce.core.type.ProductIdSupport;
import com.geecommerce.core.type.Versionable;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public abstract class AbstractRepositorySupport implements RepositorySupport {
    protected static final String MULTI_CTX_QUERY_CTX_CACHE_NAME = "gc/mctx-query/ctx";
    protected static final String MULTI_CTX_QUERY_ONE_CTX_CACHE_NAME = "gc/mctx-query/one_ctx";

    protected static final String QUERY_CACHE_NAME_PREFIX = "gc/query/repository/";
    protected static final String MODEL_CACHE_NAME_PREFIX = "gc/model/repository/";

    protected static final String FIELD_ATTR_ID = "attr_id";
    protected static final String FIELD_VAL = "val";

    @Inject
    protected CacheManager cacheManager;

    protected Dao dao;

    public Dao dao() {
        return this.dao;
    }

    public void dao(Dao dao) {
        this.dao = dao;
    }

    public <T extends Model> T findById(Class<T> modelClass, Id id) {
        return dao().findById(modelClass, id);
    }

    @Override
    public <T extends Model> T findById(Class<T> modelClass, Id id, QueryOptions queryOptions) {
        return dao().findById(modelClass, id, queryOptions);
    }

    @Override
    public <T extends Model> List<T> findByIds(Class<T> modelClass, Id[] ids) {
        return findByIds(modelClass, ids, null);
    }

    @Override
    public <T extends Model> List<T> findByIds(Class<T> modelClass, Id[] ids, QueryOptions queryOptions) {
        return dao().findByIds(modelClass, ids, queryOptions);
    }

    @Override
    public <T extends Model> T findByUniqueKey(Class<T> modelClass, String key, Object value) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put(key, value);

        List<T> results = dao().find(modelClass, filter);

        return results == null || results.size() == 0 ? null : results.get(0);
    }

    @Override
    public <T extends Model> List<T> findAll(Class<T> modelClass) {
        return dao().findAll(modelClass);
    }

    @Override
    public <T extends Model> List<T> findAll(Class<T> modelClass, QueryOptions queryOptions) {
        return dao().findAll(modelClass, queryOptions);
    }

    @Override
    public <T extends Model> List<T> find(Class<T> modelClass, String key, Object value) {
        return find(modelClass, key, value, null);
    }

    @Override
    public <T extends Model> List<T> find(Class<T> modelClass, String key, Object value, QueryOptions queryOptions) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put(key, value);

        return find(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends Model> List<T> find(Class<T> modelClass, Map<String, Object> filter) {
        return find(modelClass, filter, null);
    }

    @Override
    public <T extends Model> List<T> find(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions) {
        return dao().find(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends Model> List<Id> findIds(Class<T> modelClass) {
        return findIds(modelClass, null, null);
    }

    @Override
    public <T extends Model> List<Id> findIds(Class<T> modelClass, Map<String, Object> filter) {
        return findIds(modelClass, filter, null);
    }

    @Override
    public <T extends Model> List<Id> findIds(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions) {
        return dao().findIds(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass) {
        return findData(modelClass, null, null);
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass, Map<String, Object> filter) {
        return findData(modelClass, filter, null);
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions) {
        return dao().findData(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findDataByIds(Class<T> modelClass, Id[] ids) {
        return findDataByIds(modelClass, ids, null);
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findDataByIds(Class<T> modelClass, Id[] ids,
        QueryOptions queryOptions) {
        return dao().findDataByIds(modelClass, ids, queryOptions);
    }

    @Override
    public <T extends Model> List<?> distinct(Class<T> modelClass, String... distinctField) {
        return distinct(modelClass, null, null, distinctField);
    }

    @Override
    public <T extends Model> List<?> distinct(Class<T> modelClass, Map<String, Object> filter,
        String... distinctField) {
        return distinct(modelClass, filter, null, distinctField);
    }

    @Override
    public <T extends Model> List<?> distinct(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions, String... distinctField) {
        return dao().distinct(modelClass, filter, queryOptions, distinctField);
    }

    @Override
    public <T extends Model> T findOne(Class<T> modelClass, Map<String, Object> filter) {
        return dao().findOne(modelClass, filter);
    }

    @Override
    public <T extends Model> Long count(Class<T> modelClass, Map<String, Object> filter) {
        return dao().count(modelClass, filter);
    }

    @Override
    public <T extends Model> Long count(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions) {
        return dao().count(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends Model> T add(T entity) {
        return dao().create(entity);
    }

    @Override
    public <T extends Model> List<T> addAll(List<T> entities) {
        List<T> createdEntities = new ArrayList<>();

        if (entities != null && entities.size() > 0) {
            for (T entity : entities) {
                createdEntities.add(dao().create(entity));
            }
        }

        return createdEntities;
    }

    @Override
    public <T extends Model> void update(T entity) {
        dao().update(entity);
    }

    @Override
    public <T extends Model> void update(T entity, Map<String, Object> filter) {
        dao().update(entity, filter);
    }

    @Override
    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert) {
        dao().update(entity, filter, upsert);
    }

    @Override
    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert, boolean multi) {
        dao().update(entity, filter, upsert, multi);
    }

    @Override
    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert, boolean multi,
        String... updateFields) {
        dao().update(entity, filter, upsert, multi, updateFields);
    }

    @Override
    public <T extends Model> void updateAll(List<T> entities) {
        if (entities != null && entities.size() > 0) {
            for (T entity : entities) {
                dao().update(entity);
            }
        }
    }

    @Override
    public <T extends Model> void remove(T entity) {
        dao().delete(entity);
    }

    @Override
    public <T extends Model> void remove(Class<T> modelClass, Map<String, Object> filter) {
        dao().delete(modelClass, filter);
    }

    @Override
    public <T extends Model> T findSnapshot(Class<T> modelClass, Id id, Integer version) {
        return dao().findSnapshot(modelClass, id, version);
    }

    @Override
    public <T extends Model> T findSnapshot(Class<T> modelClass, Id id, Integer version, QueryOptions queryOptions) {
        return dao().findSnapshot(modelClass, id, version, queryOptions);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id) {
        return dao().findSnapshots(modelClass, id);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, QueryOptions queryOptions) {
        return dao().findSnapshots(modelClass, id, queryOptions);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, Integer[] versions) {
        return dao().findSnapshots(modelClass, id, versions);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, Integer[] versions,
        QueryOptions queryOptions) {
        return dao().findSnapshots(modelClass, id, versions, queryOptions);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Map<String, Object> filter) {
        return dao().findSnapshots(modelClass, filter);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions) {
        return dao().findSnapshots(modelClass, filter, queryOptions);
    }

    // Global interfaces cannot be used as cache-keys.
    protected List<Class<?>> interfaceBlackList = Lists.newArrayList(Model.class, MultiContextModel.class,
        IdSupport.class, ProductIdSupport.class, Versionable.class, Serializable.class);

    protected <T extends Model> String cacheableInterface(Class<T> modelClass) {
        if (modelClass == null)
            throw new IllegalArgumentException("ModelClass cannot be null");

        if (modelClass.isInterface())
            return modelClass.getName();

        Class<?>[] interfaces = modelClass.getInterfaces();

        if (interfaces == null || interfaces.length == 0)
            throw new IllegalArgumentException("Unable to convert modelClass '" + modelClass.getName()
                + "' to cache-name because no interfaces were present");

        if (interfaces.length == 1) {
            Class<?> iface = interfaces[0];

            if (modelClass.getName().contains(iface.getSimpleName()))
                return iface.getName();

            if (interfaceBlackList.contains(iface))
                return iface.getName();

            else
                throw new IllegalArgumentException("ModelClass " + modelClass.getName()
                    + " could not be converted to a cache-name. Make sure that its only interace is not one of "
                    + interfaceBlackList);
        } else {
            // First try the default model specific interface.
            for (Class<?> iface : interfaces) {
                if (modelClass.getName().contains(iface.getSimpleName()))
                    return iface.getName();
            }

            // If we have had not luck, try any other interface that is not in
            // the blacklist.
            for (Class<?> iface : interfaces) {
                if (interfaceBlackList.contains(iface))
                    return iface.getName();
            }

            throw new IllegalArgumentException("ModelClass " + modelClass.getName()
                + " could not be converted to a cache-name. Try giving it a model-specific interface and make sure that it is not one of "
                + interfaceBlackList);
        }
    }

    protected <T extends Model> Cache<MultiContextCacheKey, ContextType> multiContextQueryOneCtxCache(
        Class<T> modelClass) {
        return cacheManager.getCache(MULTI_CTX_QUERY_ONE_CTX_CACHE_NAME);
    }

    protected <T extends Model> Cache<MultiContextCacheKey, List<ContextType>> multiContextQueryCtxCache(
        Class<T> modelClass) {
        return cacheManager.getCache(MULTI_CTX_QUERY_CTX_CACHE_NAME);
    }

    protected <T extends Model> Cache<String, T> modelCache(Class<T> modelClass) {
        return cacheManager
            .getCache(new StringBuilder(MODEL_CACHE_NAME_PREFIX).append(cacheableInterface(modelClass)).toString());
    }

    protected <T extends Model> Cache<String, List<Id>> queryCache(Class<T> modelClass) {
        return cacheManager
            .getCache(new StringBuilder(QUERY_CACHE_NAME_PREFIX).append(cacheableInterface(modelClass)).toString());
    }

    @Override
    public <T extends Model> void clearCaches(Class<T> modelClass) {
        dao().clearCaches(modelClass);
        clearMultiContextCaches(modelClass);
    }

    protected <T extends Model> void clearMultiContextCaches(Class<T> modelClass) {
        multiContextQueryCtxCache(modelClass).emptyCache();
        multiContextQueryOneCtxCache(modelClass).emptyCache();
        modelCache(modelClass).emptyCache();
        // queryCache(modelClass).emptyCache();
    }

    public enum ContextType {
        REQUEST, STORE, MERCHANT, GLOBAL, NONE
    }
}
