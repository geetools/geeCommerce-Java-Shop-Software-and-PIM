package com.geecommerce.shipping.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface ShippingItem extends Model {

    public String getProductName();

    public ShippingItem setProductName(String productName);

    public Id getProductId();

    public ShippingItem setProductId(Id productId);

    public Integer getQuantity();

    public ShippingItem setQuantity(Integer quantity);

    public Double getWidth();

    public ShippingItem setWidth(Double width);

    public Double getHeight();

    public ShippingItem setHeight(Double height);

    public Double getDepth();

    public ShippingItem setDepth(Double depth);

    public Double getWeight();

    public ShippingItem setWeight(Double weight);

    public Double getPrice();

    public ShippingItem setPrice(Double price);

    public String getPickupStoreId();

    public ShippingItem setPickupStoreId(String pickupStoreId);

    static final class FIELD {
        public static final String ITEM_ARTICLE_ID = "article_id";
        public static final String ITEM_QUANTITY = "qty";
        public static final String ITEM_WIDTH = "width";
        public static final String ITEM_HEIGHT = "height";
        public static final String ITEM_DEPTH = "depth";
        public static final String ITEM_WEIGHT = "weight";
        public static final String ITEM_PRICE = "price";
        public static final String ITEM_PICKUP_STORE = "pickup_store";
    }
}
