package com.geecommerce.inventory.service;

import java.util.List;

import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.service.annotation.Transactional;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.Id;
import com.geecommerce.inventory.model.InventoryItem;
import com.geecommerce.inventory.repository.Stocks;
import com.google.inject.Inject;

@Service
public class DefaultStockService implements StockService {
    private final Stocks stocks;

    @Inject
    public DefaultStockService(Stocks stocks) {
        this.stocks = stocks;
    }

    @Override
    public InventoryItem getInventoryItem(Id inventoryItemId) {
        return stocks.findById(InventoryItem.class, inventoryItemId);
    }

    @Override
    public List<InventoryItem> getInventoryItems(Id productId) {
        return stocks.belongingToProduct(productId);
    }

    @Override
    public List<InventoryItem> getInventoryItems(Id productId, Store store) {
        return stocks.belongingToProduct(productId, store);
    }

    @Override
    public List<InventoryItem> getInventoryItems(Id... productIds) {
        return stocks.belongingToProducts(productIds);
    }

    @Override
    public List<InventoryItem> getInventoryItems(Id[] productIds, Store store) {
        return stocks.belongingToProducts(productIds, store);
    }

    @Override
    public InventoryItem createInventoryItem(InventoryItem inventoryItem) {
        return stocks.add(inventoryItem);
    }

    @Override
    public void updateInventoryItem(InventoryItem inventoryItem) {
        stocks.update(inventoryItem);
    }

    @Override
    public void removeInventoryItem(InventoryItem inventoryItem) {
        stocks.remove(inventoryItem);
    }

    @Override
    @Transactional
    public void updateInventoryItems(List<InventoryItem> inventoryItems) {
        for (InventoryItem inventoryItem : inventoryItems) {
            if (inventoryItem.getId() == null || inventoryItem.getProductId() == null || inventoryItem.getQty() == null)
                throw new NullPointerException("Id, productId and quantity must be set when updating inventory items");

            stocks.update(inventoryItem);
        }
    }
}
