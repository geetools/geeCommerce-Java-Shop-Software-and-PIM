package com.geecommerce.retail.service;

import java.util.List;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.retail.model.RetailStore;
import com.geecommerce.retail.model.Warehouse;

public interface WarehouseService extends Service {

    List<Warehouse> findNearbyWarehouse(String zipCode);

    RetailStore findByNumber(String storeNumber);

    List<RetailStore> findByNumber(List<String> storeNumber);
}
