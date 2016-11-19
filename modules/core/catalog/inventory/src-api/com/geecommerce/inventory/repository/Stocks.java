package com.geecommerce.inventory.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.Id;
import com.geecommerce.inventory.exception.QuantityNotAvailableException;
import com.geecommerce.inventory.model.InventoryItem;

public interface Stocks extends Repository {
    public List<InventoryItem> belongingToProduct(Id productId);

    public List<InventoryItem> belongingToProduct(Id productId, Store store);

    public List<InventoryItem> belongingToProducts(Id... productIds);

    public List<InventoryItem> belongingToProducts(Id[] productIds, Store store);

    public Map<String, Object> getStockData(Id productId, Store store);

    public void preloadStockData(Collection<Id> productIds, Id storeId);

    public void decrementQty(Id productId, Store store, int decrementByQty) throws QuantityNotAvailableException;
}
