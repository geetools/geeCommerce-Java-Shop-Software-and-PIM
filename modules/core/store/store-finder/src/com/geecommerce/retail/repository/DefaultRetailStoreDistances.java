package com.geecommerce.retail.repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.model.RetailStore;
import com.geecommerce.retail.model.RetailStoreDistance;
import com.google.inject.Inject;

@Repository
public class DefaultRetailStoreDistances extends AbstractRepository implements RetailStoreDistances {
    private final MongoDao mongoDao;
    private final RetailStores retailStores;

    @Inject
    public DefaultRetailStoreDistances(MongoDao mongoDao, RetailStores retailStores) {
        this.mongoDao = mongoDao;
        this.retailStores = retailStores;
    }

    @Override
    public Dao dao() {
        return mongoDao;
    }

    public RetailStore closestTo(String zipCode) {
        List<RetailStore> retailStoreList = closestTo(zipCode, null);
        return retailStoreList.isEmpty() ? null : retailStoreList.get(0);
    }

    public List<RetailStore> closestTo(String zipCode, Integer count) {
        Map<String, Object> regex = new HashMap<>();
        regex.put("$regex", new StringBuilder('^').append(zipCode).toString());

        Map<String, Object> filter = new HashMap<>();
        filter.put(RetailStoreDistance.Column.FROM_ZIP, regex);

        QueryOptions.Builder queryOptionsBuilder = QueryOptions.builder().sortBy(RetailStoreDistance.Column.DISTANCE)
            .fromOffset(0);
        if (count != null) {
            queryOptionsBuilder.limitTo(count);
        }
        return findStoresByDistance(filter, queryOptionsBuilder.build());
    }

    @Override
    public Double distance(String zipCode, Id storeId) {
        RetailStore retailStore = retailStores.findById(RetailStore.class, storeId);

        return null;
    }

    private List<RetailStore> findStoresByDistance(Map<String, Object> filter, QueryOptions queryOptions) {
        List<RetailStore> retailStoreList = new LinkedList<>();

        List<RetailStoreDistance> retailStoreDistances = find(RetailStoreDistance.class, filter, queryOptions);
        if (retailStoreDistances != null && !retailStoreDistances.isEmpty()) {
            for (RetailStoreDistance retailStoreDistance : retailStoreDistances) {
                RetailStore retailStore = retailStores.findById(RetailStore.class,
                    retailStoreDistance.getToRetailStore());
                retailStoreList.add(retailStore);
            }
        }
        return retailStoreList;
    }
}
