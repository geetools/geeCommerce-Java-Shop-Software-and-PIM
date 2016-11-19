package com.geecommerce.retail.repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.retail.model.RetailStore;
import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Repository
public class DefaultRetailStores extends AbstractRepository implements RetailStores {
    private final MongoDao mongoDao;

    @Inject
    public DefaultRetailStores(MongoDao mongoDao) {
        this.mongoDao = mongoDao;
    }

    @Override
    public Dao dao() {
        return mongoDao;
    }

    @Override
    public List<RetailStore> enabledRetailStores() {
        Map<String, Object> filter = new HashMap<>();
        filter.put(RetailStore.Column.ENABLED, true);
        List<RetailStore> retailStores = multiContextFind(RetailStore.class, filter, "id2", QueryOptions.builder().sortBy(RetailStore.Column.SORT_INDEX).build());
        return retailStores;
    }

    @Override
    public RetailStore havingId2(String id2) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(RetailStore.Column.ID2, id2);
        List<RetailStore> retailStores = multiContextFind(RetailStore.class, filter, "id2");
        return retailStores == null || retailStores.isEmpty() ? null : retailStores.get(0);
    }

    @Override
    public List<RetailStore> findByNumbers(List<String> numbers) {
        Map<String, Object> filterWarehouses = new HashMap<>();
        filterWarehouses.put(RetailStore.Column.ID2, CollectionUtils.collect(numbers, new Transformer() {
            @Override
            public Object transform(Object input) {
                String store = (String) input;
                return store.substring(1, store.length());
            }
        }));
        return mongoDao.find(RetailStore.class, filterWarehouses);
    }

    @Override
    public RetailStore findByZipCode(String zipCode) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(RetailStore.Column.ZIP, zipCode);
        return mongoDao.findOne(RetailStore.class, filter);
    }

    public List<RetailStore> getStoresHavingField(String fieldName) {
        Map<String, Object> filter = new HashMap<>();
        DBObject notEmptyClause = new BasicDBObject();
        notEmptyClause.put("$nin", Arrays.asList("", null));
        filter.put(fieldName, notEmptyClause);
        return mongoDao.find(RetailStore.class, filter, (new QueryOptions.Builder().sortBy("name").build()));
    }
}
