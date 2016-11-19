package com.geecommerce.retail.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.model.RetailStoreInventory;

public interface RetailStoreInventories extends Repository {
    public List<RetailStoreInventory> thatBelongTo(Id productId);
}
