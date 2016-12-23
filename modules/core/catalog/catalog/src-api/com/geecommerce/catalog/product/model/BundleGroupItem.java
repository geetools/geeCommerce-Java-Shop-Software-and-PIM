package com.geecommerce.catalog.product.model;

import com.geecommerce.catalog.product.enums.BundleGroupType;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

import java.util.List;

public interface BundleGroupItem extends Model {

    public ContextObject<String> getLabel();

    public BundleGroupItem setLabel(ContextObject<String> label);

    public List<BundleProductItem> getBundleItems();

    public BundleGroupItem setBundleItems(List<BundleProductItem> bundleProductItems);

    public BundleGroupItem setOptional(boolean optional);

    public boolean isOptional();

    public BundleGroupItem setType(BundleGroupType type);

    public BundleGroupType getType();

    static final class Col {
        public static final String LABEL = "label";
        public static final String TYPE = "type";
        public static final String OPTIONAL = "optional";
        public static final String BUNDLE_ITEMS = "bundle_items";
    }
}
