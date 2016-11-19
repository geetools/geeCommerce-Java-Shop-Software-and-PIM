package com.geecommerce.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.event.Event;
import com.geecommerce.core.event.Observable;
import com.geecommerce.core.event.Observer;
import com.geecommerce.core.event.ObserverThreadPool;
import com.geecommerce.core.event.Observers;
import com.geecommerce.core.event.Run;
import com.geecommerce.core.event.annotation.Observe;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.IdSupport;
import com.geecommerce.core.type.ProductIdSupport;
import com.geecommerce.core.type.Versionable;
import com.geecommerce.core.util.Arr;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public abstract class AbstractDao implements Dao {
    @Inject
    protected App app;

    protected final Connections connections;
    protected final CacheManager cacheManager;

    private static final int LIMIT_FIELDS_IN_QUERY_THRESHOLD = 15;
    private static final int LIMIT_FIELDS_IN_QUERY_DIFF_PERCENT = 40;
    private static final int CUT_QUERY_VALUE_AT = 100;

    private static final String QUERY_CACHE_NAME_PREFIX = "gc/query/";
    private static final String MODEL_CACHE_NAME_PREFIX = "gc/model/";
    private static final String COUNT_CACHE_NAME_PREFIX = "gc/count/";

    private static final String CACHE_KEY_COUNT_PREFIX = "count/";
    private static final String CACHE_KEY_QUERY_PREFIX = "query/";
    private static final String CACHE_KEY_ID_PREFIX = "id/";
    private static final String CACHE_KEY_SLASH_AT = "/@";
    private static final String CACHE_KEY_SLASH_IDS_ONLY = "/@IdsOnly";
    private static final String CACHE_KEY_NULL_VALUE = "null";

    @Inject
    public AbstractDao(Connections connections, CacheManager cacheManager) {
        this.connections = connections;
        this.cacheManager = cacheManager;
    }

    protected <T extends Model> boolean limitFieldsInQuery(Class<T> modelClass, QueryOptions queryOptions) {
        boolean limitFieldsInQuery = false;

        if (queryOptions == null || queryOptions.fieldsToInclude() == null || queryOptions.fieldsToInclude().size() == 0)
            return limitFieldsInQuery;

        int fieldCount = Reflect.getFieldCount(modelClass);

        if (fieldCount > LIMIT_FIELDS_IN_QUERY_THRESHOLD) {
            List<String> fieldsToInclude = queryOptions.fieldsToInclude();

            if (fieldsToInclude != null && fieldsToInclude.size() > 0) {
                int numFieldsToInclude = fieldsToInclude.size();
                int percentToInclude = (int) Math.ceil(((double) numFieldsToInclude / (double) fieldCount) * 100);

                if (percentToInclude < LIMIT_FIELDS_IN_QUERY_DIFF_PERCENT)
                    limitFieldsInQuery = true;
            }
        }

        return limitFieldsInQuery;
    }

    protected <T extends Model> Cache<String, T> modelCache(Class<T> modelClass) {
        return cacheManager.getCache(new StringBuilder(MODEL_CACHE_NAME_PREFIX).append(cacheableInterface(modelClass)).toString());
    }

    protected <T extends Model> Cache<String, List<Id>> queryCache(Class<T> modelClass) {
        return cacheManager.getCache(new StringBuilder(QUERY_CACHE_NAME_PREFIX).append(cacheableInterface(modelClass)).toString());
    }

    protected <T extends Model> Cache<String, Long> countCache(Class<T> modelClass) {
        return cacheManager.getCache(new StringBuilder(COUNT_CACHE_NAME_PREFIX).append(cacheableInterface(modelClass)).toString());
    }

    protected <T extends Model> boolean isCacheable(Class<T> modelClass) {
        return Annotations.isCacheable(modelClass);
    }

    protected <T extends Model> boolean isCacheable(Class<T> modelClass, QueryOptions queryOptions) {
        return (queryOptions == null || !queryOptions.isNoCache()) && Annotations.isCacheable(modelClass);
    }

    protected boolean isRefresh(QueryOptions queryOptions) {
        return (queryOptions != null && queryOptions.isRefresh()) || app.refreshHeaderExists();
    }

    protected boolean isFromCacheOnly(QueryOptions queryOptions) {
        return queryOptions != null && queryOptions.isFromCacheOnly();
    }

    protected <T extends Model> boolean isPreloadableModel(Class<T> modelClass) {
        Class<T> modelInterfce = Models.interfaceType(modelClass);
        Set<?> preloadableModels = Models.thatArePreloadable();

        return preloadableModels != null && preloadableModels.contains(modelInterfce);
    }

    protected boolean isPreview() {
        return app.previewHeaderExists();
    }

    protected <T extends Model> boolean modelCacheHasKeyFor(Class<T> modelClass, Id id, QueryOptions queryOptions) {
        return modelCache(modelClass).containsKey(toModelKey(modelClass, id, queryOptions));
    }

    protected <T extends Model> T cacheGet(Class<T> modelClass, Id id, QueryOptions queryOptions) {
        return modelCache(modelClass).get(toModelKey(modelClass, id, queryOptions));
    }

    protected <T extends Model> void cachePut(Class<T> modelClass, T obj, QueryOptions queryOptions) {
        modelCache(modelClass).put(toModelKey(modelClass, obj.getId(), queryOptions), obj);
    }

    protected <T extends Model> void cacheRemove(Class<T> modelClass, Id id, QueryOptions queryOptions) {
        modelCache(modelClass).remove(toModelKey(modelClass, id, queryOptions));
    }

    protected <T extends Model> void cachePutNull(Class<T> modelClass, Id id, QueryOptions queryOptions) {
        modelCache(modelClass).put(toModelKey(modelClass, id, queryOptions), null);
    }

    protected <T extends Model> boolean queryCacheHasKeyFor(Class<T> modelClass, Map<String, Object> query, QueryOptions queryOptions) {
        boolean containsKey = queryCache(modelClass).containsKey(toQueryKey(query, queryOptions));
        return containsKey;
    }

    protected <T extends Model> List<Id> cacheGet(Class<T> modelClass, Map<String, Object> query, QueryOptions queryOptions) {
        String key = toQueryKey(query, queryOptions);
        List<Id> ids = queryCache(modelClass).get(key);
        return ids;
    }

    protected <T extends Model> void cachePut(Class<T> modelClass, Map<String, Object> query, QueryOptions queryOptions, List<Id> modelIds) {
        String key = toQueryKey(query, queryOptions);
        queryCache(modelClass).put(key, modelIds);
    }

    protected <T extends Model> boolean countCacheHasKeyFor(Class<T> modelClass, Map<String, Object> query) {
        return countCache(modelClass).containsKey(toCountKey(query));
    }

    protected <T extends Model> Long countCacheGet(Class<T> modelClass, Map<String, Object> query) {
        return countCache(modelClass).get(toCountKey(query));
    }

    protected <T extends Model> void countCachePut(Class<T> modelClass, Map<String, Object> query, Long count) {
        String key = toCountKey(query);
        countCache(modelClass).put(key, count);
    }

    /**
     * Remove all cached queries for a particular model-object.
     * <p>
     * Use this when deleting an object from mongodb.
     *
     * @param objectId
     * @param queryCacheKey
     */
    protected <T extends Model> void invalidateQueryCaches(Id modelObjectId) {
        Set<String> cacheNames = cacheManager.getCacheNames();

        for (String cacheName : cacheNames) {
            // We only want to invalidate query-caches.
            if (!cacheName.startsWith(QUERY_CACHE_NAME_PREFIX))
                continue;

            Cache<Object, List<Id>> queryCache = cacheManager.getCache(cacheName);

            if (queryCache != null) {
                Object[] queryCacheKeys = queryCache.keySet();

                if (queryCacheKeys != null && queryCacheKeys.length > 0) {
                    List<Object> keysToRemove = new ArrayList<>();

                    for (Object key : queryCacheKeys) {
                        List<Id> ids = queryCache.get(key);

                        if (ids != null && ids.contains(modelObjectId)) {
                            keysToRemove.add(key);
                        }
                    }

                    for (Object queryToRemove : keysToRemove) {
                        queryCache.remove(queryToRemove);
                    }
                }
            }
        }
    }

    @Override
    public <T extends Model> void clearCaches(Class<T> modelClass) {
        invalidateModelCache(modelClass);
        invalidateQueryCache(modelClass);
    }

    /**
     * Remove all cached queries for a particular model-type.
     * <p>
     * Use this when inserting a new object into mongodb.
     *
     * @param modelClass
     */
    protected <T extends Model> void invalidateQueryCache(Class<T> modelClass) {
        Cache<String, List<Id>> queryCache = queryCache(modelClass);

        if (queryCache != null)
            queryCache.emptyCache();
    }

    /**
     * Remove all cached models for a particular model-type.
     * <p>
     * Use this when inserting or updating an object into mongodb.
     *
     * @param modelClass
     */
    protected <T extends Model> void invalidateModelCache(Class<T> modelClass) {
        Cache<String, T> modelCache = modelCache(modelClass);

        if (modelCache != null)
            modelCache.emptyCache();
    }

    protected String toCountKey(Map<String, Object> query) {
        StringBuilder key = new StringBuilder(CACHE_KEY_COUNT_PREFIX);
        appendQueryPart(key, query);

        // System.out.println(key.toString());

        return key.toString();
    }

    protected String toQueryKey(Map<String, Object> query, QueryOptions queryOptions) {
        StringBuilder key = new StringBuilder(CACHE_KEY_QUERY_PREFIX);
        appendQueryPart(key, query);

        if (queryOptions != null)
            key.append(Char.SLASH).append(queryOptions.toCacheKey());

        // System.out.println(key.toString());

        return key.toString();
    }

    @SuppressWarnings("unchecked")
    protected void appendQueryPart(StringBuilder key, Object query) {
        if (query == null) {
            key.append(CACHE_KEY_NULL_VALUE);
            return;
        }

        Class<?> c = query.getClass();

        // --MAP--
        if (query instanceof Map<?, ?>) {
            Map<String, Object> queryMap = (Map<String, Object>) query;
            if (queryMap.size() > 0) {
                Set<String> keys = queryMap.keySet();
                int y = 0;

                key.append(Char.CURLY_BRACKET_OPEN);

                for (String k : keys) {
                    if (y > 0)
                        key.append(Char.SEMI_COLON);

                    Object v = queryMap.get(k);
                    key.append(k).append(Char.EQUALS);
                    appendQueryPart(key, v);

                    y++;
                }

                key.append(Char.CURLY_BRACKET_CLOSE);

            } else {
                key.append(CACHE_KEY_NULL_VALUE);
                return;
            }
            // --COLLECTION--
        } else if (c.isArray() || query instanceof Collection<?>) {
            Collection<?> col = c.isArray() ? Arr.asCollection(query) : (Collection<?>) query;

            StringBuilder colVal = new StringBuilder();

            key.append(Char.SQUARE_BRACKET_OPEN);

            int y = 0;
            for (Object o : col) {
                if (y > 0)
                    colVal.append(Char.COMMA);

                appendQueryPart(colVal, o);

                y++;
            }

            if (colVal.length() > CUT_QUERY_VALUE_AT) {
                int hashCode = colVal.toString().hashCode();
                key.append(colVal.substring(0, CUT_QUERY_VALUE_AT)).append(Char.HASH).append(hashCode);
            } else {
                key.append(colVal);
            }

            key.append(Char.SQUARE_BRACKET_CLOSE);

        } else {
            String s = String.valueOf(query);

            if (s.length() > CUT_QUERY_VALUE_AT) {
                int hashCode = s.hashCode();
                key.append(s.substring(0, CUT_QUERY_VALUE_AT)).append(Char.HASH).append(hashCode);
            } else {
                key.append(s);
            }
        }
    }

    protected <T extends Model> String toModelKey(Class<T> modelClass, Id id, QueryOptions queryOptions) {
        boolean limitFieldsInQuery = limitFieldsInQuery(modelClass, queryOptions);

        if (queryOptions != null && (limitFieldsInQuery || queryOptions.attributesToInclude() != null || queryOptions.attributesToExclude() != null)) {
            StringBuilder keySuffix = new StringBuilder();

            if (limitFieldsInQuery)
                keySuffix.append(StringUtils.join(queryOptions.fieldsToInclude(), ',').hashCode());

            if (queryOptions.attributesToInclude() != null && !queryOptions.attributesToInclude().isEmpty())
                keySuffix.append(StringUtils.join(queryOptions.attributesToInclude(), ',').hashCode());

            if (queryOptions.attributesToExclude() != null && !queryOptions.attributesToExclude().isEmpty())
                keySuffix.append(StringUtils.join(queryOptions.attributesToExclude(), ',').hashCode());

            return new StringBuilder(CACHE_KEY_ID_PREFIX).append(id.str()).append(CACHE_KEY_SLASH_AT).append(keySuffix).toString();
        } else if (queryOptions != null && queryOptions.isFetchIdsOnly()) {
            return new StringBuilder(CACHE_KEY_ID_PREFIX).append(id.str()).append(CACHE_KEY_SLASH_IDS_ONLY).toString();
        } else {
            return new StringBuilder(CACHE_KEY_ID_PREFIX).append(id.str()).toString();
        }
    }

    // Global interfaces cannot be used as cache-keys.
    protected List<Class<?>> interfaceBlackList = Lists.newArrayList(Model.class, MultiContextModel.class, IdSupport.class, ProductIdSupport.class, Versionable.class, Serializable.class);

    protected <T extends Model> String cacheableInterface(Class<T> modelClass) {
        if (modelClass == null)
            throw new IllegalArgumentException("Model-class cannot be null");

        String interfaceName = Models.interfaceName(modelClass);

        if (interfaceName == null)
            throw new IllegalArgumentException("Unable to find cacheable interface for model-class '" + modelClass.getName() + "'");

        return interfaceName;
    }

    protected <T extends Observable> void notify(Observable observable, Event event) {
        if (!app.isApplicationInitialized())
            return;

        List<Class<Observer>> observerClasses = Observers.forClass(observable.getClass(), event);

        if (observerClasses.size() == 0)
            return;

        for (final Class<Observer> observerClass : observerClasses) {
            Observer observer = app.inject(observerClass);
            Observe declaredAnnotation = com.geecommerce.core.utils.Annotations.declaredAnnotation(observerClass, Observe.class);

            boolean isEnabled = isEnabledContext(declaredAnnotation, observerClass);

            if (isEnabled) {
                // Run asynchronously with thread pool.
                if (declaredAnnotation.run() == Run.ASYNCHRONOUSLY) {
                    ObserverThreadPool.INSTANCE.run(observable, event, observer);
                }
            }
        }

        // Now run the synchronous observers which may run in parallel to the
        // asynchronous ones.
        for (final Class<? extends Observer> observerClass : observerClasses) {
            Observer observer = app.inject(observerClass);
            Observe declaredAnnotation = com.geecommerce.core.utils.Annotations.declaredAnnotation(observerClass, Observe.class);

            boolean isEnabled = isEnabledContext(declaredAnnotation, observerClass);

            if (isEnabled) {
                // Run synchronously
                if (declaredAnnotation.run() == Run.SYNCHRONOUSLY) {
                    observer.onEvent(event, observable);
                }
            }
        }
    }

    private static final String IS_OBSERVER_ENABLED_PREFIX = "observers/";
    private static final String IS_OBSERVER_ENABLED_SUFFIX = "/enabled";

    private boolean isEnabledContext(final Observe observeAnnotation, final Class<? extends Observer> observerClass) {
        // -------------------------------------------------------
        // Check configuration.
        // -------------------------------------------------------
        Boolean isEnabled = app.cpBool_(new StringBuilder(IS_OBSERVER_ENABLED_PREFIX).append(observerClass.getName()).append(IS_OBSERVER_ENABLED_SUFFIX).toString());

        if (isEnabled != null)
            return isEnabled;

        ApplicationContext appCtx = app.getApplicationContext();

        // -------------------------------------------------------
        // Check annotation views.
        // -------------------------------------------------------
        View view = appCtx.getView();

        String[] views = observeAnnotation.views();

        if (view != null && views != null && views.length > 0) {
            // If views have been specified, make default false until we find
            // match.
            isEnabled = false;

            for (String viewCode : views) {
                if (viewCode != null && viewCode.equals(view.getCode())) {
                    isEnabled = true;
                    break;
                }
            }

            return isEnabled;
        }

        // -------------------------------------------------------
        // Check annotation stores.
        // -------------------------------------------------------
        Store store = appCtx.getStore();

        String[] stores = observeAnnotation.stores();

        if (store != null && stores != null && stores.length > 0) {
            // If stores have been specified, make default false until we find
            // match.
            isEnabled = false;

            for (String storeCode : stores) {
                if (storeCode != null && storeCode.equals(store.getCode())) {
                    isEnabled = true;
                    break;
                }
            }

            return isEnabled;
        }

        // -------------------------------------------------------
        // Check annotation merchants.
        // -------------------------------------------------------
        Merchant merchant = appCtx.getMerchant();

        String[] merchants = observeAnnotation.merchants();

        if (merchant != null && merchants != null && merchants.length > 0) {
            // If merchants have been specified, make default false until we
            // find match.
            isEnabled = false;

            for (String merchantCode : merchants) {
                if (merchantCode != null && merchantCode.equals(merchant.getCode())) {
                    isEnabled = true;
                    break;
                }
            }

            return isEnabled;
        }

        return true;
    }

    protected boolean isDateRange(Object value) {
        if (parseDateRange(value) != null) {
            return true;
        }
        return false;
    }

    protected JSONObject parseDateRange(Object value) {
        JSONObject dateRange = null;
        try {
            String dateObject = Joiner.on(" ").join((Iterable<?>) value);
            JSONObject o = (JSONObject) new JSONParser().parse(dateObject);
            dateRange = (JSONObject) o.get("dateRange");
            if (dateRange != null) {
                return dateRange;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
