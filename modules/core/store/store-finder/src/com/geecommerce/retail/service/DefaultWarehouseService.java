package com.geecommerce.retail.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.service.persistence.mysql.MySqlDao;
import com.geecommerce.retail.model.RetailStore;
import com.geecommerce.retail.model.Warehouse;
import com.geecommerce.retail.repository.RetailStores;
import com.google.inject.Inject;

@Service
public class DefaultWarehouseService implements WarehouseService {
    private final MySqlDao sqlDao;
    private final RetailStores retailStores;

    @Inject
    public DefaultWarehouseService(RetailStores retailStores, MySqlDao sqlDao) {
        this.retailStores = retailStores;
        this.sqlDao = sqlDao;
    }

    public RetailStore findByNumber(String storeNumber) {
        return findByNumber(Collections.singletonList(storeNumber)).get(0);
    }

    public List<RetailStore> findByNumber(List<String> storeNumbers) {
        List<RetailStore> result = new LinkedList<>();
        storeNumbers.stream().forEach(storeNumber -> result.addAll(retailStores.find(RetailStore.class, "id2", storeNumber)));
        return result;
    }

    public List<Warehouse> findNearbyWarehouse(String zipCode) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("plz", zipCode);
        List<Warehouse> warehouses = sqlDao.find(Warehouse.class, filter);

        return warehouses;
    }
}
