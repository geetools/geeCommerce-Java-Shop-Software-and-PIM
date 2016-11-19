package com.geecommerce.retail.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.model.RetailStoreInventory;
import com.geecommerce.retail.model.RetailStoreInventory.Column;

@Repository
public class DefaultRetailStoreInventories extends AbstractRepository implements RetailStoreInventories {
    @Override
    public List<RetailStoreInventory> thatBelongTo(Id productId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(Column.PRODUCT_ID, productId);

        return find(RetailStoreInventory.class, filter);
    }
}
