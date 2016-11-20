package com.geecommerce.cart.model;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.model.Price;
import com.geecommerce.price.model.PriceType;

public interface CartItem extends Model {
    public Id getProductId();

    public CartItem setProductId(Id productId);

    public Product getProduct();

    public CartItem setProduct(Product product);

    public String getProductName();

    public String getProductURI();

    public Price productPrice();

    public Double getProductPrice();

    public PriceType getProductPriceType();

    public Double getProductTaxRate();

    public int getQuantity();

    public CartItem setQuantity(int quantity);

    public CartItem incrementQty();

    public Double getPackageWeight();

    public Double getPackageWidth();

    public Double getPackageHeight();

    public Double getPackageDepth();

    public Boolean isLast();

    public CartItem setLast(Boolean last);

    public Boolean isActive();

    public Boolean getActive();

    public CartItem setActive(Boolean isActive);

    public Boolean getDeliveryAvailable();

    public CartItem setDeliveryAvailable(Boolean deliveryAvailable);

    public Boolean getPickupAvailable();

    public CartItem setPickupAvailable(Boolean pickupAvailable);

    public String getDeliveryMethod();

    public CartItem setDeliveryMethod(String deliveryMethod);

    public Boolean isPickup();

    public Boolean isPickupAvailable(String pickupStoreId);

    public Boolean getPickup();

    public CartItem setPickup(Boolean isPickup);

    public String getPickupStoreId();

    public CartItem setPickupStoreId(String storeId);

    public Boolean isDeliveryAvailable(String zip);

    public String getPickupDeliveryTime();

    static final class Column {
        public static final String PRODUCT_ID = "prd_id";
        public static final String PRODUCT_NAME = "prd_name";
        public static final String PRODUCT_PRICE = "prd_price";
        public static final String PRODUCT_PRICE_TYPE_ID = "prd_price_type_id";
        public static final String PRODUCT_TAX_RATE = "prd_tax_rate";
        public static final String PRODUCT_PACKAGE_WEIGHT = "prd_pkg_weight";
        public static final String PRODUCT_PACKAGE_WIDTH = "prd_pkg_width";
        public static final String PRODUCT_PACKAGE_HEIGHT = "prd_pkg_height";
        public static final String PRODUCT_PACKAGE_DEPTH = "prd_pkg_depth";
        public static final String QUANTITY = "qty";
        public static final String LAST = "is_last";
        public static final String DELIVERY_METHOD = "delivery_method";
        public static final String ACTIVE = "prd_active";
        public static final String PICKUP = "prd_pickup";
        public static final String PICKUP_AVAILABLE = "prd_pickup_available";
        public static final String PICKUP_STORE_ID = "prd_pickup_store_id";
    }
}
