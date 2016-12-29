package com.geecommerce.catalog.product.model;


import com.geecommerce.catalog.product.enums.BundleGroupType;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Model
public class DefaultBundleGroupItem extends AbstractModel implements BundleGroupItem {

    @Column(Col.ID)
    protected Id id = null;

    @Column(Col.LABEL)
    protected ContextObject<String> label = null;

    @Column(Col.TYPE)
    protected BundleGroupType type = null;

    @Column(Col.OPTIONAL)
    protected boolean optional = false;

    @Column(Col.SHOW_IN_PRODUCT_DETAILS)
    protected boolean showInProductDetails = true;


    protected List<BundleProductItem> bundleProductItems = null;


    @Override
    public BundleGroupItem setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public BundleGroupItem setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public List<BundleProductItem> getBundleItems() {
        return bundleProductItems;
    }

    @Override
    public BundleGroupItem setBundleItems(List<BundleProductItem> bundleProductItems) {
        this.bundleProductItems = bundleProductItems;
        return this;
    }

    @Override
    public BundleGroupItem setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public BundleGroupItem setShowInProductDetails(boolean showInProductDetails) {
        this.showInProductDetails = showInProductDetails;
        return this;
    }

    @Override
    public Boolean getShowInProductDetails() {
        return showInProductDetails;
    }

    @Override
    public BundleGroupItem setType(BundleGroupType type) {
        this.type = type;
        return this;
    }

    @Override
    public BundleGroupType getType() {
        return type;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;
        super.fromMap(map);
        this.id = id_(map.get(Col.ID));
        this.label = ctxObj_(map.get(Col.LABEL));
        this.type = enum_(BundleGroupType.class, map.get(Col.TYPE));
        this.optional = bool_(map.get(Col.OPTIONAL), false);
        this.showInProductDetails = bool_(map.get(Col.SHOW_IN_PRODUCT_DETAILS), true);


        List<Map<String, Object>> items = list_(map.get(Col.BUNDLE_ITEMS));
        if (items != null && items.size() > 0) {
            this.bundleProductItems = new ArrayList<>();
            for (Map<String, Object> item : items) {
                BundleProductItem bundleProductItem = app.model(BundleProductItem.class);
                bundleProductItem.fromMap(item);
                this.bundleProductItems.add(bundleProductItem);
            }
        }

    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(Col.ID, getId());
        map.put(Col.LABEL, getLabel());
        map.put(Col.TYPE, getType());
        map.put(Col.OPTIONAL, isOptional());
        map.put(Col.SHOW_IN_PRODUCT_DETAILS, getShowInProductDetails());

        if (getBundleItems() != null && getBundleItems().size() > 0){
            List<Map<String, Object>> items = new ArrayList<>();
            for (BundleProductItem bundleProductItem : bundleProductItems) {
                items.add(bundleProductItem.toMap());
            }
            map.put(Col.BUNDLE_ITEMS, items);
        } else {
            map.put(Col.BUNDLE_ITEMS, null);
        }

        return map;
    }
}
