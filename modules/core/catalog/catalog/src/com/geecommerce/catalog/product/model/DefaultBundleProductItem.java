package com.geecommerce.catalog.product.model;


import com.geecommerce.catalog.product.enums.BundleConditionType;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Model
public class DefaultBundleProductItem extends AbstractModel implements BundleProductItem {

    protected final ProductService productService;

    @Column(Col.PRODUCT_ID)
    protected Id productId = null;

    @Column(Col.DEFAULT_PRODUCT_ID)
    protected Id defaultProductId = null;

    @Column(Col.QUANTITY)
    protected int quantity = 1;

    @Column(Col.SELECTED)
    protected boolean selected = false;

    @Column(Col.WITH_PRODUCTS)
    protected List<Id> withProductIds = null;

    @Column(Col.CONDITION_TYPE)
    protected BundleConditionType conditionType = null;

    protected Product product = null;

    @Inject
    public DefaultBundleProductItem(ProductService productService) {
        this.productService = productService;
    }

    public DefaultBundleProductItem(){
        productService = app.service(ProductService.class);
    }

    @Override
    public Id getProductId() {
        return productId;
    }

    @Override
    public BundleProductItem setProductId(Id productId) {
        this.productId = productId;
        this.product = null;
        return this;
    }

    @Override
    public Id getDefaultProductId() {
        return defaultProductId;
    }

    @Override
    public BundleProductItem setDefaultProductId(Id id) {
        this.defaultProductId = id;
        return this;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public BundleProductItem setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public Product getProduct() {
        if(product == null && productId != null){
            product = productService.getProduct(productId);
        }
        return product;
    }

    @Override
    public BundleProductItem setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public List<Id> getWithProductIds() {
        return withProductIds;
    }

    @Override
    public BundleProductItem setWithProductIds(List<Id> withProductIds) {
        this.withProductIds = withProductIds;
        return this;
    }

    @Override
    public BundleProductItem addWithProductId(Id productId) {
        if(withProductIds == null)
            withProductIds = new ArrayList<>();
        withProductIds.add(productId);
        return this;
    }

    @Override
    public BundleConditionType getConditionType() {
        return conditionType;
    }

    @Override
    public BundleProductItem setWithProductIds(BundleConditionType conditionType) {
        this.conditionType = conditionType;
        return this;
    }

    @Override
    public Id getId() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;
        super.fromMap(map);
        this.productId = id_(map.get(Col.PRODUCT_ID));
        this.quantity = int_(map.get(Col.QUANTITY), 1);
        this.defaultProductId = id_(map.get(Col.DEFAULT_PRODUCT_ID));
        this.selected = bool_(map.get(Col.SELECTED), false);
        this.withProductIds = idList_(map.get(Col.WITH_PRODUCTS));
        this.conditionType = enum_(BundleConditionType.class, map.get(Col.CONDITION_TYPE));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(Col.PRODUCT_ID, getProductId());
        m.put(Col.QUANTITY, getQuantity());
        m.put(Col.DEFAULT_PRODUCT_ID, getDefaultProductId());
        m.put(Col.SELECTED, isSelected());
        m.put(Col.WITH_PRODUCTS, getWithProductIds());
        m.put(Col.CONDITION_TYPE, getConditionType());

        return m;
    }
}
