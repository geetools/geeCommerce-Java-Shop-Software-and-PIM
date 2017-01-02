package com.geecommerce.catalog.product.model;

import com.geecommerce.catalog.product.enums.BundleGroupType;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

import java.util.List;

public interface BundleGroupItem extends Model {

    public BundleGroupItem setId(Id id);

    public ContextObject<String> getLabel();

    public BundleGroupItem setLabel(ContextObject<String> label);

    public List<BundleProductItem> getBundleItems();

    public BundleGroupItem setBundleItems(List<BundleProductItem> bundleProductItems);

    public BundleGroupItem setOptional(boolean optional);

    public boolean isOptional();

    public BundleGroupItem setShowInProductDetails(boolean showInProductDetails);

    public Boolean getShowInProductDetails();

    public BundleGroupItem setType(BundleGroupType type);

    public BundleGroupType getType();

    public boolean hasItemsValidForSelling();

    public boolean allItemsValidForSelling();

    public List<BundleProductItem> getValidBundleItems();

    static final class Col {
        public static final String ID = "_id";
        public static final String LABEL = "label";
        public static final String TYPE = "type";
        public static final String OPTIONAL = "optional";
        public static final String SHOW_IN_PRODUCT_DETAILS = "show_in_prd_details";
        public static final String BUNDLE_ITEMS = "bundle_items";
    }
}
