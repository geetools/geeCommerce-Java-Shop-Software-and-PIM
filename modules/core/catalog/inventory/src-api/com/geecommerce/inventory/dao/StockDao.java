package com.geecommerce.inventory.dao;

import java.util.Collection;
import java.util.Map;

import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.type.Id;
import com.geecommerce.inventory.exception.QuantityNotAvailableException;

public interface StockDao extends Dao {
    public Map<String, Object> getStockData(Id productId, Id requestCtxId);

    public void preloadStockData(Collection<Id> productIds, Id storeId);

    public void decrementQty(Id productId, Id requestCtxId, int decrementByQty) throws QuantityNotAvailableException;
}
