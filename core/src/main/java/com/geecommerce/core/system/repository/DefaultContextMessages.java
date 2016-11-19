package com.geecommerce.core.system.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.core.service.persistence.mongodb.MongoQueries;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.ContextMessage;
import com.geecommerce.core.system.model.RequestContext;
import com.google.inject.Inject;

public class DefaultContextMessages extends AbstractRepository implements ContextMessages {
    final MongoDao mongoDao;

    @Inject
    public DefaultContextMessages(MongoDao mongoDao) {
        this.mongoDao = mongoDao;
    }

    @Override
    public Dao dao() {
        return this.mongoDao;
    }

    /**
     * Find context message for the specified request-context.
     */
    @Override
    public ContextMessage havingKey(String key, RequestContext reqCtx) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addRequestContextFilter(filter, reqCtx);

        filter.put(ContextMessage.Column.KEY, key);

        return findOne(ContextMessage.class, filter);
    }

    /**
     * Find context message for the specified store.
     */
    @Override
    public ContextMessage havingKey(String key, Store store) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addStoreFilter(filter, store);

        filter.put(ContextMessage.Column.KEY, key);

        return findOne(ContextMessage.class, filter);
    }

    /**
     * Find context message for the specified merchant.
     */
    @Override
    public ContextMessage havingKey(String key, Merchant merchant) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addMerchantFilter(filter, merchant);

        filter.put(ContextMessage.Column.KEY, key);

        return findOne(ContextMessage.class, filter);
    }

    /**
     * Find global context message.
     */
    @Override
    public ContextMessage havingGlobalKey(String key) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addGlobalScopeFilter(filter);

        filter.put(ContextMessage.Column.KEY, key);

        return findOne(ContextMessage.class, filter);
    }

    /**
     * Find context message closest to the current context (search-order:
     * RequestContext, Store, Merchant, Global-Scope).
     */
    @Override
    public ContextMessage havingKey(String key) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(ContextMessage.Column.KEY, key);

        return multiContextFindOne(ContextMessage.class, filter);
    }

    /**
     * Find all messages that belong to the current context. This includes the
     * requestContext, store, merchant and global scopes.
     */
    @Override
    public List<ContextMessage> inContext(String key) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(ContextMessage.Column.KEY, key);

        return simpleContextFind(ContextMessage.class, filter);
    }

    /**
     * Find context messages that match the regular-expression for the specified
     * request-context.
     */
    @Override
    public List<ContextMessage> havingKeysLike(String regex, RequestContext reqCtx) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addRequestContextFilter(filter, reqCtx);

        Map<String, Object> regexMap = new LinkedHashMap<>();
        regexMap.put("$regex", regex);

        filter.put(ContextMessage.Column.KEY, regexMap);

        return find(ContextMessage.class, filter);
    }

    /**
     * Find context messages that match the regular-expression for the specified
     * store.
     */
    @Override
    public List<ContextMessage> havingKeysLike(String regex, Store store) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addStoreFilter(filter, store);

        Map<String, Object> regexMap = new LinkedHashMap<>();
        regexMap.put("$regex", regex);

        filter.put(ContextMessage.Column.KEY, regexMap);

        return find(ContextMessage.class, filter);
    }

    /**
     * Find context messages that match the regular-expression for the specified
     * merchant.
     */
    @Override
    public List<ContextMessage> havingKeysLike(String regex, Merchant merchant) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addMerchantFilter(filter, merchant);

        Map<String, Object> regexMap = new LinkedHashMap<>();
        regexMap.put("$regex", regex);

        filter.put(ContextMessage.Column.KEY, regexMap);

        return find(ContextMessage.class, filter);
    }

    /**
     * Find global context messages that match the regular-expression.
     */
    @Override
    public List<ContextMessage> havingGlobalKeysLike(String regex) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addGlobalScopeFilter(filter);

        Map<String, Object> regexMap = new LinkedHashMap<>();
        regexMap.put("$regex", regex);

        filter.put(ContextMessage.Column.KEY, regexMap);

        return find(ContextMessage.class, filter);
    }

    /**
     * Find context messages closest to the current context (search-order:
     * RequestContext, Store, Merchant, Global-Scope) that match the
     * regular-expression.
     */
    @Override
    public List<ContextMessage> havingKeysLike(String regex) {
        Map<String, Object> filter = new LinkedHashMap<>();

        Map<String, Object> regexMap = new LinkedHashMap<>();
        regexMap.put("$regex", regex);

        filter.put(ContextMessage.Column.KEY, regexMap);

        return multiContextFind(ContextMessage.class, filter, ContextMessage.Column.KEY);
    }
}
