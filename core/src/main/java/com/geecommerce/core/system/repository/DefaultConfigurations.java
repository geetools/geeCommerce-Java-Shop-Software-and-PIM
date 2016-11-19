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
import com.geecommerce.core.system.model.ConfigurationProperty;
import com.geecommerce.core.system.model.RequestContext;
import com.google.inject.Inject;

public class DefaultConfigurations extends AbstractRepository implements Configurations {
    final MongoDao mongoDao;

    @Inject
    public DefaultConfigurations(MongoDao mongoDao) {
        this.mongoDao = mongoDao;
    }

    @Override
    public Dao dao() {
        return this.mongoDao;
    }

    /**
     * Find configuration property for the specified request-context.
     */
    @Override
    public ConfigurationProperty havingKey(String key, RequestContext reqCtx) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addRequestContextFilter(filter, reqCtx);

        filter.put(ConfigurationProperty.Column.KEY, key);

        return findOne(ConfigurationProperty.class, filter);
    }

    /**
     * Find configuration property for the specified store.
     */
    @Override
    public ConfigurationProperty havingKey(String key, Store store) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addStoreFilter(filter, store);

        filter.put(ConfigurationProperty.Column.KEY, key);

        return findOne(ConfigurationProperty.class, filter);
    }

    /**
     * Find configuration property for the specified merchant.
     */
    @Override
    public ConfigurationProperty havingKey(String key, Merchant merchant) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addMerchantFilter(filter, merchant);

        filter.put(ConfigurationProperty.Column.KEY, key);

        return findOne(ConfigurationProperty.class, filter);
    }

    /**
     * Find global configuration property.
     */
    @Override
    public ConfigurationProperty havingGlobalKey(String key) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addGlobalScopeFilter(filter);

        filter.put(ConfigurationProperty.Column.KEY, key);

        return findOne(ConfigurationProperty.class, filter);
    }

    /**
     * Find configuration property closest to the current context (search-order:
     * RequestContext, Store, Merchant, Global-Scope).
     */
    @Override
    public ConfigurationProperty havingKey(String key) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(ConfigurationProperty.Column.KEY, key);

        return multiContextFindOne(ConfigurationProperty.class, filter);
    }

    /**
     * Find configuration properties that match the regular-expression for the
     * specified request-context.
     */
    @Override
    public List<ConfigurationProperty> havingKeysLike(String regex, RequestContext reqCtx) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addRequestContextFilter(filter, reqCtx);

        Map<String, Object> regexMap = new LinkedHashMap<>();
        regexMap.put("$regex", regex);

        filter.put(ConfigurationProperty.Column.KEY, regexMap);

        return find(ConfigurationProperty.class, filter);
    }

    /**
     * Find configuration properties that match the regular-expression for the
     * specified store.
     */
    @Override
    public List<ConfigurationProperty> havingKeysLike(String regex, Store store) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addStoreFilter(filter, store);

        Map<String, Object> regexMap = new LinkedHashMap<>();
        regexMap.put("$regex", regex);

        filter.put(ConfigurationProperty.Column.KEY, regexMap);

        return find(ConfigurationProperty.class, filter);
    }

    /**
     * Find configuration properties that match the regular-expression for the
     * specified merchant.
     */
    @Override
    public List<ConfigurationProperty> havingKeysLike(String regex, Merchant merchant) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addMerchantFilter(filter, merchant);

        Map<String, Object> regexMap = new LinkedHashMap<>();
        regexMap.put("$regex", regex);

        filter.put(ConfigurationProperty.Column.KEY, regexMap);

        return find(ConfigurationProperty.class, filter);
    }

    /**
     * Find global configuration properties that match the regular-expression.
     */
    @Override
    public List<ConfigurationProperty> havingGlobalKeysLike(String regex) {
        Map<String, Object> filter = new LinkedHashMap<>();
        MongoQueries.addGlobalScopeFilter(filter);

        Map<String, Object> regexMap = new LinkedHashMap<>();
        regexMap.put("$regex", regex);

        filter.put(ConfigurationProperty.Column.KEY, regexMap);

        return find(ConfigurationProperty.class, filter);
    }

    /**
     * Find configuration properties closest to the current context
     * (search-order: RequestContext, Store, Merchant, Global-Scope) that match
     * the
     * regular-expression.
     */
    @Override
    public List<ConfigurationProperty> havingKeysLike(String regex) {
        Map<String, Object> filter = new LinkedHashMap<>();

        Map<String, Object> regexMap = new LinkedHashMap<>();
        regexMap.put("$regex", regex);

        filter.put(ConfigurationProperty.Column.KEY, regexMap);

        return multiContextFind(ConfigurationProperty.class, filter, ConfigurationProperty.Column.KEY);
    }
}
