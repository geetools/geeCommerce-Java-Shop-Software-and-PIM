package com.geecommerce.retail.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.model.RetailStore;
import com.geecommerce.retail.model.Warehouse;

import java.util.List;

public interface WarehouseService extends Service {

    List<Warehouse> findNearbyWarehouse(String zipCode);

    RetailStore findByNumber(String storeNumber);

    List<RetailStore> findByNumber(List<String> storeNumber);
}
