package com.geecommerce.inventory.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.Id;
import com.geecommerce.inventory.dao.StockDao;
import com.geecommerce.inventory.exception.QuantityNotAvailableException;
import com.geecommerce.inventory.model.InventoryItem;
import com.google.inject.Inject;

@Repository
public class DefaultStocks extends AbstractRepository implements Stocks {
    private final StockDao stockDao;

    @Inject
    public DefaultStocks(StockDao stockDao) {
        this.stockDao = stockDao;
    }

    @Override
    public Dao dao() {
        return this.stockDao;
    }

    @Override
    public List<InventoryItem> belongingToProduct(Id productId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(InventoryItem.Col.PRODUCT_ID, productId);

        return find(InventoryItem.class, filter);
    }

    @Override
    public List<InventoryItem> belongingToProduct(Id productId, Store store) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(InventoryItem.Col.PRODUCT_ID, productId);
        filter.put(InventoryItem.Col.STORE_ID, store.getId());

        return find(InventoryItem.class, filter);
    }

    @Override
    public List<InventoryItem> belongingToProducts(Id... productIds) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(InventoryItem.Col.PRODUCT_ID, productIds);

        return find(InventoryItem.class, filter);
    }

    @Override
    public List<InventoryItem> belongingToProducts(Id[] productIds, Store store) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(InventoryItem.Col.PRODUCT_ID, productIds);
        filter.put(InventoryItem.Col.STORE_ID, store.getId());

        return find(InventoryItem.class, filter);
    }

    @Override
    public Map<String, Object> getStockData(Id productId, Store store) {
        return stockDao.getStockData(productId, store.getId());
    }

    public void preloadStockData(Collection<Id> productIds, Id storeId) {
        stockDao.preloadStockData(productIds, storeId);
    }

    @Override
    public void decrementQty(Id productId, Store store, int decrementByQty) throws QuantityNotAvailableException {
        stockDao.decrementQty(productId, store.getId(), decrementByQty);
    }
}
