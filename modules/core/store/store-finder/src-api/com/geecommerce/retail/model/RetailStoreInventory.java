package com.geecommerce.retail.model;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.Id;

public interface RetailStoreInventory extends MultiContextModel {
    public Id getId();

    public RetailStoreInventory setId(Id id);

    public Id getRetailStoreId();

    public RetailStore getRetailStore();

    public RetailStoreInventory setRetailStoreId(Id retailStoreId);

    public Id getProductId();

    public RetailStoreInventory setProductId(Id productId);

    public Id getAvailabilityTextId();

    public String getAvailabilityText();

    public RetailStoreInventory setAvailabilityTextId(Id availabilityTextId);

    public Integer getQuantity();

    public RetailStoreInventory setQuantity(Integer quantity);

    static final class Column {
	public static final String ID = "_id";
	public static final String RETAIL_STORE_ID = "retail_store_id";
	public static final String PRODUCT_ID = "product_id";
	public static final String AVAILABILITY_TEXT_ID = "availability_text_id";
	public static final String QUANTITY = "qty";
    }
}
