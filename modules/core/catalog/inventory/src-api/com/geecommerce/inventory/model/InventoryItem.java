package com.geecommerce.inventory.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface InventoryItem extends Model {
    public static final String TABLE_NAME = "inventory_stock";

    public Id getId();

    public InventoryItem setId(Id id);

    public Id getProductId();

    public InventoryItem setProductId(Id productId);

    public Id getStoreId();

    public InventoryItem setStoreId(Id storeId);

    public Integer getQty();

    public InventoryItem setQty(Integer qty);

    public boolean isAllowBackorder();

    public InventoryItem setAllowBackorder(boolean allowBackorder);

    static final class Col {
        public static final String ID = "_id";
        public static final String PRODUCT_ID = "prd_id";
        public static final String STORE_ID = "store_id";
        public static final String QTY = "qty";
        public static final String ALLOW_BACKORDER = "backorder";
    }
}
