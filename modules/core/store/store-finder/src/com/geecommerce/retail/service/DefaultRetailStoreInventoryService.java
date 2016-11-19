package com.geecommerce.retail.service;

import java.util.List;

import com.google.inject.Inject;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.model.RetailStoreInventory;
import com.geecommerce.retail.repository.RetailStoreInventories;


@Service
public class DefaultRetailStoreInventoryService implements RetailStoreInventoryService {
    private final RetailStoreInventories retailStoreInventories;

    @Inject
    public DefaultRetailStoreInventoryService(RetailStoreInventories retailStoreInventories) {
	this.retailStoreInventories = retailStoreInventories;
    }

    
    @Override
    public RetailStoreInventory createRetailStoreInventory(RetailStoreInventory retailStoreInventory) {
	return retailStoreInventories.add(retailStoreInventory);
    }

    
    @Override
    public void update(RetailStoreInventory retailStoreInventory) {
	retailStoreInventories.update(retailStoreInventory);
    }

    
    @Override
    public List<RetailStoreInventory> getRetailStoreInventoriesByProductId(Id productId) {
	return retailStoreInventories.thatBelongTo(productId);
    }
}
