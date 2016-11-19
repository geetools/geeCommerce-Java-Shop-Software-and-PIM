package com.geecommerce.retail.service;

import java.util.List;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.model.RetailStoreInventory;

public interface RetailStoreInventoryService extends Service {
    public RetailStoreInventory createRetailStoreInventory(RetailStoreInventory retailStoreInventory);

    public void update(RetailStoreInventory retailStoreInventory);

    public List<RetailStoreInventory> getRetailStoreInventoriesByProductId(Id productId);
}
