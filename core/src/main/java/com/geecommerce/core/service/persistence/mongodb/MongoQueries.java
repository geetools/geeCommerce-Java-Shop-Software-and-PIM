package com.geecommerce.core.service.persistence.mongodb;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;

public class MongoQueries {
    public static void addCtxObjFilter(Map<String, Object> targetFilter, String key, Object value) {
        Map<String, Object> valuePart = new LinkedHashMap<String, Object>();
        valuePart.put(ContextObject.VALUE, value);
        Map<String, Object> elemMatchPart = new LinkedHashMap<String, Object>();
        elemMatchPart.put(QueryOperators.ELEM_MATCH, valuePart);

        targetFilter.put(key, elemMatchPart);
    }

    public static void addCtxObjFilter(Map<String, Object> targetFilter, String key, Object value, String language) {
        Map<String, Object> valuePart = new LinkedHashMap<String, Object>();
        valuePart.put(ContextObject.VALUE, value);
        valuePart.put(ContextObject.LANGUAGE, language);

        Map<String, Object> elemMatchPart = new LinkedHashMap<String, Object>();
        elemMatchPart.put(QueryOperators.ELEM_MATCH, valuePart);

        targetFilter.put(key, elemMatchPart);
    }

    public static void addCtxObjFilter(Map<String, Object> targetFilter, String key, Object value, Store store) {
        Map<String, Object> valuePart = new LinkedHashMap<String, Object>();
        valuePart.put(ContextObject.VALUE, value);
        valuePart.put(ContextObject.STORE, store.getId());

        Map<String, Object> elemMatchPart = new LinkedHashMap<String, Object>();
        elemMatchPart.put(QueryOperators.ELEM_MATCH, valuePart);

        targetFilter.put(key, elemMatchPart);
    }

    public static void addMerchantFilter(Map<String, Object> targetFilter) {
        addMerchantFilter(targetFilter, null);
    }

    public static void addMerchantFilter(Map<String, Object> targetFilter, Merchant merchant) {
        if (merchant == null) {
            ApplicationContext appCtx = App.get().getApplicationContext();
            merchant = appCtx.getMerchant();
        }

        if (merchant != null && merchant.getId() != null) {
            DBObject inClause = new BasicDBObject();
            inClause.put(QueryOperators.IN, new Id[] { merchant.getId() });

            targetFilter.put(GlobalColumn.MERCHANT_ID, inClause);
        }
    }

    public static void addStoreFilter(Map<String, Object> targetFilter) {
        addStoreFilter(targetFilter, null);
    }

    public static void addStoreFilter(Map<String, Object> targetFilter, Store store) {
        if (store == null) {
            ApplicationContext appCtx = App.get().getApplicationContext();
            store = appCtx.getStore();
        }

        if (store != null && store.getId() != null) {
            DBObject inClause = new BasicDBObject();
            inClause.put(QueryOperators.IN, new Id[] { store.getId() });

            targetFilter.put(GlobalColumn.STORE_ID, inClause);
        }
    }

    public static void addRequestContextFilter(Map<String, Object> targetFilter) {
        addRequestContextFilter(targetFilter, null);
    }

    public static void addRequestContextFilter(Map<String, Object> targetFilter, RequestContext reqCtx) {
        if (reqCtx == null) {
            ApplicationContext appCtx = App.get().getApplicationContext();
            reqCtx = appCtx.getRequestContext();
        }

        if (reqCtx != null && reqCtx.getId() != null) {
            DBObject inClause = new BasicDBObject();
            inClause.put(QueryOperators.IN, new Id[] { reqCtx.getId() });

            targetFilter.put(GlobalColumn.REQUEST_CONTEXT_ID, inClause);
        }
    }

    public static void addGlobalScopeFilter(Map<String, Object> targetFilter) {
        targetFilter.put(GlobalColumn.REQUEST_CONTEXT_ID, null);
        targetFilter.put(GlobalColumn.STORE_ID, null);
        targetFilter.put(GlobalColumn.MERCHANT_ID, null);
    }

    public static Map<String, Object> newFilter(String field, Object value) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(field, value);

        return filter;
    }
}
