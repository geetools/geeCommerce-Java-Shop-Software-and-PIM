package com.geecommerce.inventory.service;

import java.util.List;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.Id;
import com.geecommerce.inventory.model.InventoryItem;

public interface StockService extends Service {
    public InventoryItem getInventoryItem(Id inventoryItemId);

    public List<InventoryItem> getInventoryItems(Id productId);

    public List<InventoryItem> getInventoryItems(Id productId, Store store);

    public List<InventoryItem> getInventoryItems(Id... productId);

    public List<InventoryItem> getInventoryItems(Id[] productIds, Store store);

    public InventoryItem createInventoryItem(InventoryItem inventoryItem);

    public void updateInventoryItem(InventoryItem inventoryItem);

    public void removeInventoryItem(InventoryItem inventoryItem);

    public void updateInventoryItems(List<InventoryItem> inventoryItems);
}
