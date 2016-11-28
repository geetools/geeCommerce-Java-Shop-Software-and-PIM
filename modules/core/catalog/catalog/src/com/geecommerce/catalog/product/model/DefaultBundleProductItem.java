package com.geecommerce.catalog.product.model;


import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

import java.util.LinkedHashMap;
import java.util.Map;

@Model
public class DefaultBundleProductItem extends AbstractModel implements BundleProductItem {

    protected final ProductService productService;

    @Column(Col.PRODUCT_ID)
    protected Id productId = null;

    @Column(Col.QUANTITY)
    protected int quantity = 1;

    protected Product product = null;

    @Inject
    public DefaultBundleProductItem(ProductService productService) {
        this.productService = productService;
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
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(Col.PRODUCT_ID, getProductId());
        m.put(Col.QUANTITY, getQuantity());

        return m;
    }
}
