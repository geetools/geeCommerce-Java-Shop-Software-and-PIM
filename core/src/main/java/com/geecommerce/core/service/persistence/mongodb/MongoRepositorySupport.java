package com.geecommerce.core.service.persistence.mongodb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.db.annotation.Persistence;
import com.geecommerce.core.service.AbstractRepositorySupport;
import com.geecommerce.core.service.Annotations;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.MultiContextCacheKey;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.RepositorySupport;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.system.attribute.Attributes;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.mongodb.QueryOperators;

@RepositorySupport
@Persistence("mongodb")
public class MongoRepositorySupport extends AbstractRepositorySupport {
    @Inject
    protected App app;

    @Override
    public <T extends MultiContextModel> T multiContextFindOne(Class<T> modelClass, Map<String, Object> filter) {
        boolean isCacheableInRepository = Annotations.isCacheableInRepository(modelClass);
        boolean isRefresh = app.refreshHeaderExists();
        boolean isPreview = app.previewHeaderExists();

        Cache<MultiContextCacheKey, ContextType> contextsCache = multiContextQueryOneCtxCache(modelClass);

        MultiContextCacheKey contextKey = new MultiContextCacheKey(modelClass, filter, null, null);
        Cache<String, T> cache = null;
        if (isCacheableInRepository)
            cache = modelCache(modelClass);

        if (isCacheableInRepository && !isRefresh && !isPreview && cache.containsKey(contextKey.toString())) {
            return cache.get(contextKey.toString());
        } else {
            ContextType contextType = contextsCache.get(contextKey);
            if (contextType != null && contextType.equals(ContextType.NONE))
                return null;

            T result = null;
            // ----------------------------------------------------------------
            // First attempt to find values for the "RequestContext" scope.
            // ----------------------------------------------------------------
            if (contextType == null || contextType.equals(ContextType.REQUEST)) {
                Map<String, Object> reqCtxFilter = new LinkedHashMap<>(filter);
                MongoQueries.addRequestContextFilter(reqCtxFilter);

                if (reqCtxFilter.containsKey(GlobalColumn.REQUEST_CONTEXT_ID))
                    result = dao().findOne(modelClass, reqCtxFilter);

                if (result != null) {
                    if (contextType == null)
                        contextsCache.put(contextKey, ContextType.REQUEST);
                    if (isCacheableInRepository && !isPreview)
                        cache.put(contextKey.toString(), result);
                    return result;
                }
            }

            // ----------------------------------------------------------------
            // Next attempt to find values for the "Store" scope.
            // ----------------------------------------------------------------
            if (contextType == null || contextType.equals(ContextType.STORE)) {
                Map<String, Object> storeFilter = new LinkedHashMap<>(filter);
                MongoQueries.addStoreFilter(storeFilter);

                if (storeFilter.containsKey(GlobalColumn.STORE_ID))
                    result = dao().findOne(modelClass, storeFilter);

                if (result != null) {
                    if (contextType == null)
                        contextsCache.put(contextKey, ContextType.STORE);
                    if (isCacheableInRepository && !isPreview)
                        cache.put(contextKey.toString(), result);
                    return result;
                }
            }

            // ----------------------------------------------------------------
            // Next attempt to find values for the "Merchant" scope.
            // ----------------------------------------------------------------
            if (contextType == null || contextType.equals(ContextType.MERCHANT)) {
                Map<String, Object> merchantFilter = new LinkedHashMap<>(filter);
                MongoQueries.addMerchantFilter(merchantFilter);

                if (merchantFilter.containsKey(GlobalColumn.MERCHANT_ID))
                    result = dao().findOne(modelClass, merchantFilter);

                if (result != null) {
                    if (contextType == null)
                        contextsCache.put(contextKey, ContextType.MERCHANT);
                    if (isCacheableInRepository && !isPreview)
                        cache.put(contextKey.toString(), result);
                    return result;
                }
            }

            // ----------------------------------------------------------------
            // Next attempt to find values for the global scope.
            // ----------------------------------------------------------------
            if (contextType == null || contextType.equals(ContextType.GLOBAL)) {
                Map<String, Object> globalFilter = new LinkedHashMap<>(filter);
                MongoQueries.addGlobalScopeFilter(globalFilter);

                result = dao().findOne(modelClass, globalFilter);
                if (result != null) {
                    if (contextType == null)
                        contextsCache.put(contextKey, ContextType.GLOBAL);
                    if (isCacheableInRepository && !isPreview)
                        cache.put(contextKey.toString(), result);
                    return result;
                }
            }

            if (contextType == null)
                contextsCache.put(contextKey, ContextType.NONE);
            return result;
        }
    }

    public <T extends MultiContextModel> List<T> multiContextFind(Class<T> modelClass, Map<String, Object> filter, String distinctFieldName) {
        return multiContextFind(modelClass, filter, distinctFieldName, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends MultiContextModel> List<T> multiContextFind(Class<T> modelClass, Map<String, Object> filter, String distinctFieldName, QueryOptions queryOptions) {
        List<MultiContextModel> allResults = new ArrayList<>();

        Cache<MultiContextCacheKey, List<ContextType>> contextsCache = multiContextQueryCtxCache(modelClass);

        Map<String, Object> notInClause = null;

        MultiContextCacheKey contextsKey = new MultiContextCacheKey(modelClass, filter, distinctFieldName, queryOptions);
        List<ContextType> contextTypes = contextsCache.get(contextsKey);

        List<ContextType> existsContextTypes = new ArrayList<>();
        // ----------------------------------------------------------------
        // First attempt to find values for the "RequestContext" scope.
        // ----------------------------------------------------------------

        if (contextTypes == null || contextTypes.contains(ContextType.REQUEST)) {
            Map<String, Object> reqCtxFilter = new LinkedHashMap<>(filter);
            MongoQueries.addRequestContextFilter(reqCtxFilter);

            List<T> reqCtxResults = dao().find(modelClass, reqCtxFilter, queryOptions);

            if (reqCtxResults != null && !reqCtxResults.isEmpty()) {
                // Add request-context results to the complete list.
                allResults.addAll(reqCtxResults);

                // We need to filter out results that we have already received
                // on the request-context level, so that
                // we do not get them again for the store, merchant or global
                // scopes.
                notInClause = getNotInClauseForAlreadyFetchedResults(allResults, distinctFieldName);
            }

            if (reqCtxResults != null && reqCtxResults.size() > 0)
                existsContextTypes.add(ContextType.REQUEST);
        }

        // ----------------------------------------------------------------
        // Next attempt to find values for the "Store" scope.
        // ----------------------------------------------------------------

        if (contextTypes == null || contextTypes.contains(ContextType.STORE)) {
            Map<String, Object> storeFilter = new LinkedHashMap<>(filter);
            MongoQueries.addStoreFilter(storeFilter);

            if (notInClause != null && notInClause.size() > 0) {
                // Build an AND query if the main map already has the key.
                if (storeFilter.containsKey(distinctFieldName)) {
                    Map<String, Object> existingValue = new LinkedHashMap<>();
                    existingValue.put(distinctFieldName, storeFilter.get(distinctFieldName));

                    Map<String, Object> newValue = new LinkedHashMap<>();
                    newValue.put(distinctFieldName, notInClause);

                    // Remove old value and replace it with the new $and filter.
                    storeFilter.remove(distinctFieldName);
                    storeFilter.put(QueryOperators.AND, Lists.newArrayList(existingValue, newValue));
                } else {
                    // Exclude the results that we have already got.
                    storeFilter.put(distinctFieldName, notInClause);
                }
            }

            List<T> storeResults = dao().find(modelClass, storeFilter, queryOptions);

            // Add store results to the complete list.
            allResults.addAll(storeResults);

            // Again, filter out results that we have already received, so that
            // we do not get them again for the merchant or global scopes.
            notInClause = getNotInClauseForAlreadyFetchedResults(allResults, distinctFieldName);

            if (storeResults != null && storeResults.size() > 0)
                existsContextTypes.add(ContextType.STORE);
        }

        // ----------------------------------------------------------------
        // Next attempt to find values for the "Merchant" scope.
        // ----------------------------------------------------------------
        if (contextTypes == null || contextTypes.contains(ContextType.MERCHANT)) {
            Map<String, Object> merchantFilter = new LinkedHashMap<>(filter);
            MongoQueries.addMerchantFilter(merchantFilter);

            if (notInClause != null && notInClause.size() > 0) {
                // Build an AND query if the main map already has the key.
                if (merchantFilter.containsKey(distinctFieldName)) {
                    Map<String, Object> existingValue = new LinkedHashMap<>();
                    existingValue.put(distinctFieldName, merchantFilter.get(distinctFieldName));

                    Map<String, Object> newValue = new LinkedHashMap<>();
                    newValue.put(distinctFieldName, notInClause);

                    // Remove old value and replace it with the new $and filter.
                    merchantFilter.remove(distinctFieldName);
                    merchantFilter.put(QueryOperators.AND, Lists.newArrayList(existingValue, newValue));
                } else {
                    // Exclude the results that we have already got.
                    merchantFilter.put(distinctFieldName, notInClause);
                }
            }

            List<T> merchantResults = dao().find(modelClass, merchantFilter, queryOptions);

            // Add store results to the complete list.
            allResults.addAll(merchantResults);

            // Again, filter out results that we have already received, so that
            // we do not get them again for the merchant or global scopes.
            notInClause = getNotInClauseForAlreadyFetchedResults(allResults, distinctFieldName);

            if (merchantResults != null && merchantResults.size() > 0)
                existsContextTypes.add(ContextType.MERCHANT);
        }

        // ----------------------------------------------------------------
        // Next attempt to find values for the global scope.
        // ----------------------------------------------------------------

        if (contextTypes == null || contextTypes.contains(ContextType.GLOBAL)) {
            Map<String, Object> globalFilter = new LinkedHashMap<>(filter);
            MongoQueries.addGlobalScopeFilter(globalFilter);

            if (notInClause != null && notInClause.size() > 0) {
                // Build an AND query if the main map already has the key.
                if (globalFilter.containsKey(distinctFieldName)) {
                    Map<String, Object> existingValue = new LinkedHashMap<>();
                    existingValue.put(distinctFieldName, globalFilter.get(distinctFieldName));

                    Map<String, Object> newValue = new LinkedHashMap<>();
                    newValue.put(distinctFieldName, notInClause);

                    // Remove old value and replace it with the new $and filter.
                    globalFilter.remove(distinctFieldName);
                    globalFilter.put(QueryOperators.AND, Lists.newArrayList(existingValue, newValue));
                } else {
                    // Exclude the results that we have already got.
                    globalFilter.put(distinctFieldName, notInClause);
                }
            }

            List<T> globalResults = dao().find(modelClass, globalFilter, queryOptions);

            // Add store results to the complete list.
            allResults.addAll(globalResults);

            if (globalResults != null && globalResults.size() > 0)
                existsContextTypes.add(ContextType.GLOBAL);
        }

        if (contextTypes == null) {
            contextsCache.put(contextsKey, existsContextTypes);
        }

        return (List<T>) allResults;
    }

    @Override
    public <T extends MultiContextModel> T simpleContextFindOne(Class<T> modelClass, Map<String, Object> filter) {
        List<T> found = simpleContextFind(modelClass, filter, null);

        return found == null || found.size() == 0 ? null : found.get(0);
    }

    @Override
    public <T extends MultiContextModel> List<T> simpleContextFind(Class<T> modelClass, Map<String, Object> filter) {
        return simpleContextFind(modelClass, filter, null);
    }

    /**
     * Composes a request like the following: {$or: [ {$and: [{"status": 1},
     * {m:null}, {s:null}, {req_ctx_id:null}]}, {$and: [{"status": 1},
     * {m:2605912398810100}, {s:2605912398810101},
     * {req_ctx_id:2605912416310100}]},
     * <p>
     * {$and: [{"status": 1}, {m:2605912398810100}, {s:null},
     * {req_ctx_id:null}]}, {$and: [{"status": 1}, {m:2605912398810100},
     * {s:2605912398810101},
     * {req_ctx_id:null}]}, {$and: [{"status": 1}, {m:2605912398810100},
     * {s:null}, {req_ctx_id:2605912416310100}]},
     * <p>
     * {$and: [{"status": 1}, {m:null}, {s:2605912398810101},
     * {req_ctx_id:null}]}, {$and: [{"status": 1}, {m:null},
     * {s:2605912398810101},
     * {req_ctx_id:2605912416310100}]},
     * <p>
     * {$and: [{"status": 1}, {m:null}, {s:null},
     * {req_ctx_id:2605912416310100}]} ]}
     * <p>
     * Finds all documents that belong to the current context.
     *
     * @param modelClass
     * @param filter
     * @param queryOptions
     * @return
     */
    @Override
    public <T extends MultiContextModel> List<T> simpleContextFind(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions) {
        ApplicationContext appCtx = app.getApplicationContext();
        RequestContext reqCtx = appCtx.getRequestContext();

        Map<String, Object> rootORMap = new LinkedHashMap<>();

        List<Map<String, Object>> ctxANDMaps = new ArrayList<>();

        // ----------------------------------------------------------
        // All context values are null (global scope)
        // ----------------------------------------------------------
        Map<String, Object> ctxANDMap1 = new LinkedHashMap<>(filter);
        ctxANDMap1.put(GlobalColumn.MERCHANT_ID, null);
        ctxANDMap1.put(GlobalColumn.STORE_ID, null);
        ctxANDMap1.put(GlobalColumn.REQUEST_CONTEXT_ID, null);
        ctxANDMaps.add(ctxANDMap1);

        if (reqCtx != null) {
            // ----------------------------------------------------------
            // All context values are set
            // ----------------------------------------------------------
            Map<String, Object> ctxANDMap2 = new LinkedHashMap<>(filter);
            ctxANDMap2.put(GlobalColumn.MERCHANT_ID, reqCtx.getMerchantId());
            ctxANDMap2.put(GlobalColumn.STORE_ID, reqCtx.getStoreId());
            ctxANDMap2.put(GlobalColumn.REQUEST_CONTEXT_ID, reqCtx.getId());
            ctxANDMaps.add(ctxANDMap2);

            // ----------------------------------------------------------
            // Only merchant is set
            // ----------------------------------------------------------
            Map<String, Object> ctxANDMap3 = new LinkedHashMap<>(filter);
            ctxANDMap3.put(GlobalColumn.MERCHANT_ID, reqCtx.getMerchantId());
            ctxANDMap3.put(GlobalColumn.STORE_ID, null);
            ctxANDMap3.put(GlobalColumn.REQUEST_CONTEXT_ID, null);
            ctxANDMaps.add(ctxANDMap3);

            // ----------------------------------------------------------
            // Only store is set
            // ----------------------------------------------------------
            Map<String, Object> ctxANDMap6 = new LinkedHashMap<>(filter);
            ctxANDMap6.put(GlobalColumn.MERCHANT_ID, null);
            ctxANDMap6.put(GlobalColumn.STORE_ID, reqCtx.getStoreId());
            ctxANDMap6.put(GlobalColumn.REQUEST_CONTEXT_ID, null);
            ctxANDMaps.add(ctxANDMap6);

            // ----------------------------------------------------------
            // Only request_context is set
            // ----------------------------------------------------------
            Map<String, Object> ctxANDMap8 = new LinkedHashMap<>(filter);
            ctxANDMap8.put(GlobalColumn.MERCHANT_ID, null);
            ctxANDMap8.put(GlobalColumn.STORE_ID, null);
            ctxANDMap8.put(GlobalColumn.REQUEST_CONTEXT_ID, reqCtx.getId());
            ctxANDMaps.add(ctxANDMap8);
        }

        rootORMap.put(QueryOperators.OR, ctxANDMaps);

        List<T> ctxResults = dao().find(modelClass, rootORMap, queryOptions);

        return (List<T>) ctxResults;
    }

    @Override
    public <T extends MultiContextModel> List<Id> simpleContextFindIdsOnly(Class<T> modelClass, Map<String, Object> filter) {
        return simpleContextFindIdsOnly(modelClass, filter, null);
    }

    @Override
    public <T extends MultiContextModel> List<Id> simpleContextFindIdsOnly(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions) {
        ApplicationContext appCtx = app.getApplicationContext();
        RequestContext reqCtx = appCtx.getRequestContext();

        Map<String, Object> rootORMap = new LinkedHashMap<>();

        List<Map<String, Object>> ctxANDMaps = new ArrayList<>();

        // ----------------------------------------------------------
        // All context values are null (global scope)
        // ----------------------------------------------------------
        Map<String, Object> ctxANDMap1 = new LinkedHashMap<>(filter);
        ctxANDMap1.put(GlobalColumn.MERCHANT_ID, null);
        ctxANDMap1.put(GlobalColumn.STORE_ID, null);
        ctxANDMap1.put(GlobalColumn.REQUEST_CONTEXT_ID, null);
        ctxANDMaps.add(ctxANDMap1);

        // A job may not have the request-context initialized.
        if (reqCtx != null) {
            // ----------------------------------------------------------
            // All context values are set
            // ----------------------------------------------------------
            Map<String, Object> ctxANDMap2 = new LinkedHashMap<>(filter);
            ctxANDMap2.put(GlobalColumn.MERCHANT_ID, reqCtx.getMerchantId());
            ctxANDMap2.put(GlobalColumn.STORE_ID, reqCtx.getStoreId());
            ctxANDMap2.put(GlobalColumn.REQUEST_CONTEXT_ID, reqCtx.getId());
            ctxANDMaps.add(ctxANDMap2);

            // ----------------------------------------------------------
            // Only merchant is set
            // ----------------------------------------------------------
            Map<String, Object> ctxANDMap3 = new LinkedHashMap<>(filter);
            ctxANDMap3.put(GlobalColumn.MERCHANT_ID, reqCtx.getMerchantId());
            ctxANDMap3.put(GlobalColumn.STORE_ID, null);
            ctxANDMap3.put(GlobalColumn.REQUEST_CONTEXT_ID, null);
            ctxANDMaps.add(ctxANDMap3);

            // ----------------------------------------------------------
            // Only store is set
            // ----------------------------------------------------------
            Map<String, Object> ctxANDMap6 = new LinkedHashMap<>(filter);
            ctxANDMap6.put(GlobalColumn.MERCHANT_ID, null);
            ctxANDMap6.put(GlobalColumn.STORE_ID, reqCtx.getStoreId());
            ctxANDMap6.put(GlobalColumn.REQUEST_CONTEXT_ID, null);
            ctxANDMaps.add(ctxANDMap6);

            // ----------------------------------------------------------
            // Only request_context is set
            // ----------------------------------------------------------
            Map<String, Object> ctxANDMap8 = new LinkedHashMap<>(filter);
            ctxANDMap8.put(GlobalColumn.MERCHANT_ID, null);
            ctxANDMap8.put(GlobalColumn.STORE_ID, null);
            ctxANDMap8.put(GlobalColumn.REQUEST_CONTEXT_ID, reqCtx.getId());
            ctxANDMaps.add(ctxANDMap8);
        }

        rootORMap.put(QueryOperators.OR, ctxANDMaps);

        return dao().findIds(modelClass, rootORMap, queryOptions);
    }

    public <T extends MultiContextModel> List<T> contextFind(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions) {
        ApplicationContext appCtx = app.getApplicationContext();
        RequestContext reqCtx = appCtx.getRequestContext();

        Map<String, Object> rootORMap = new LinkedHashMap<>();

        List<Map<String, Object>> ctxANDMaps = new ArrayList<>();

        // ----------------------------------------------------------
        // All context values are null (global scope)
        // ----------------------------------------------------------
        Map<String, Object> ctxANDMap1 = new LinkedHashMap<>(filter);
        ctxANDMap1.put(GlobalColumn.MERCHANT_ID, null);
        ctxANDMap1.put(GlobalColumn.STORE_ID, null);
        ctxANDMap1.put(GlobalColumn.REQUEST_CONTEXT_ID, null);
        ctxANDMaps.add(ctxANDMap1);

        // ----------------------------------------------------------
        // All context values are set
        // ----------------------------------------------------------
        Map<String, Object> ctxANDMap2 = new LinkedHashMap<>(filter);
        ctxANDMap2.put(GlobalColumn.MERCHANT_ID, reqCtx.getMerchantId());
        ctxANDMap2.put(GlobalColumn.STORE_ID, reqCtx.getStoreId());
        ctxANDMap2.put(GlobalColumn.REQUEST_CONTEXT_ID, reqCtx.getId());
        ctxANDMaps.add(ctxANDMap2);

        // ----------------------------------------------------------
        // Only merchant is set
        // ----------------------------------------------------------
        Map<String, Object> ctxANDMap3 = new LinkedHashMap<>(filter);
        ctxANDMap3.put(GlobalColumn.MERCHANT_ID, reqCtx.getMerchantId());
        ctxANDMap3.put(GlobalColumn.STORE_ID, null);
        ctxANDMap3.put(GlobalColumn.REQUEST_CONTEXT_ID, null);
        ctxANDMaps.add(ctxANDMap3);

        // ----------------------------------------------------------
        // Only store is set
        // ----------------------------------------------------------
        Map<String, Object> ctxANDMap6 = new LinkedHashMap<>(filter);
        ctxANDMap6.put(GlobalColumn.MERCHANT_ID, null);
        ctxANDMap6.put(GlobalColumn.STORE_ID, reqCtx.getStoreId());
        ctxANDMap6.put(GlobalColumn.REQUEST_CONTEXT_ID, null);
        ctxANDMaps.add(ctxANDMap6);

        // ----------------------------------------------------------
        // Only request_context is set
        // ----------------------------------------------------------
        Map<String, Object> ctxANDMap8 = new LinkedHashMap<>(filter);
        ctxANDMap8.put(GlobalColumn.MERCHANT_ID, null);
        ctxANDMap8.put(GlobalColumn.STORE_ID, null);
        ctxANDMap8.put(GlobalColumn.REQUEST_CONTEXT_ID, reqCtx.getId());
        ctxANDMaps.add(ctxANDMap8);

        rootORMap.put(QueryOperators.OR, ctxANDMaps);

        List<T> ctxResults = dao().find(modelClass, rootORMap, queryOptions);

        return (List<T>) ctxResults;
    }

    protected <T extends MultiContextModel> Map<String, Object> getNotInClauseForAlreadyFetchedResults(List<T> resultObjects, String distinctFieldName) {
        List<Object> values = getValuesByDistinctField(resultObjects, distinctFieldName);

        Map<String, Object> notInClause = new LinkedHashMap<>();

        if (values != null && values.size() > 0) {
            notInClause.put(QueryOperators.NIN, values);
        }

        return notInClause;
    }

    protected <T extends MultiContextModel> List<Object> getValuesByDistinctField(List<T> resultObjects, String distinctFieldName) {
        List<Object> values = new ArrayList<>();

        for (MultiContextModel m : resultObjects) {
            Map<String, Object> map = m.toMap();
            values.add(map.get(distinctFieldName));
        }

        return values;
    }

    @SuppressWarnings("unchecked")
    public void appendAttributeCondition(AttributeTargetObject targetObject, String attributeCode, Object value, Map<String, Object> query) {
        Map<String, Object> attributes = (Map<String, Object>) query.get(AttributeSupport.AttributeSupportColumn.ATTRIBUTES);

        // ---------------------------------------------------------------
        // As this may not be the first command to set an attribute query,
        // we make sure that we always add to an existing list.
        // ---------------------------------------------------------------
        if (attributes == null) {
            attributes = new LinkedHashMap<>();
            attributes.put(QueryOperators.ALL, new ArrayList<>());
            query.put(AttributeSupport.AttributeSupportColumn.ATTRIBUTES, attributes);
        }

        List<Map<String, Object>> allAttributes = (List<Map<String, Object>>) attributes.get(QueryOperators.ALL);

        // ---------------------------------------------------------------
        // Build the actual attribute value query part.
        // ---------------------------------------------------------------

        Map<String, Object> valueMap = new LinkedHashMap<>();
        valueMap.put(FIELD_VAL, value);

        Id attributeId = Attributes.getAttributeId(targetObject.getId(), attributeCode);

        Map<String, Object> attrVal = new LinkedHashMap<>();
        attrVal.put(FIELD_ATTR_ID, attributeId);
        attrVal.put(FIELD_VAL, elemMatch(valueMap));

        allAttributes.add(elemMatch(attrVal));
    }

    protected Map<String, Object> elemMatch(Map<String, Object> val) {
        Map<String, Object> elemMatchMap = new LinkedHashMap<>();
        elemMatchMap.put(QueryOperators.ELEM_MATCH, val);
        return elemMatchMap;
    }
}
