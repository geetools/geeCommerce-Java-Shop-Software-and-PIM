package com.geecommerce.catalog.product.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface BundleProductItem extends Model {
    public Id getProductId();

    public BundleProductItem setProductId(Id id);

    public int getQuantity();

    public BundleProductItem setQuantity(int quantity);

    public Product getProduct();

    static final class Col {
        public static final String PRODUCT_ID = "product_id";
        public static final String QUANTITY = "qty";
    }
}
