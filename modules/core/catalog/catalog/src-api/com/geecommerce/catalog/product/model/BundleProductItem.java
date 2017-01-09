package com.geecommerce.catalog.product.model;

import com.geecommerce.catalog.product.enums.BundleConditionType;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

import java.util.List;

public interface BundleProductItem extends Model {
    public Id getProductId();

    public BundleProductItem setProductId(Id id);

    public Id getDefaultProductId();

    public BundleProductItem setDefaultProductId(Id id);

    public int getQuantity();

    public BundleProductItem setQuantity(int quantity);

    public Product getProduct();

    public BundleProductItem setSelected(boolean selected);

    public boolean isSelected();

    public List<Id> getWithProductIds();

    public BundleProductItem setWithProductIds(List<Id> withProductIds);

    public BundleProductItem addWithProductId(Id productId);

    public BundleConditionType getConditionType();

    public BundleProductItem setWithProductIds(BundleConditionType conditionType);

    static final class Col {
        public static final String PRODUCT_ID = "prd_id";
        public static final String QUANTITY = "qty";
        public static final String SELECTED = "selected";
        public static final String DEFAULT_PRODUCT_ID = "def_prd_id";
        public static final String WITH_PRODUCTS = "with_products";
        public static final String CONDITION_TYPE = "condition_type";

    }
}
