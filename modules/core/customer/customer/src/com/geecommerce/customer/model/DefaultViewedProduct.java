package com.geecommerce.customer.model;

import java.util.Date;
import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Model("customer_viewed_products")
public class DefaultViewedProduct extends AbstractModel implements ViewedProduct {
    private static final long serialVersionUID = 1026609630878656797L;
    private Id id = null;
    private Id customerId = null;
    private Id productId = null;
    private Date viewedOn = null;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ViewedProduct setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getCustomerId() {
        return customerId;
    }

    @Override
    public ViewedProduct viewedBy(Customer customer) {
        if (customer == null)
            throw new IllegalArgumentException("Customer cannot be null");

        this.customerId = customer.getId();

        return this;
    }

    @Override
    public Id getProductId() {
        return productId;
    }

    @Override
    public ViewedProduct viewedProduct(Id productId) {
        this.productId = productId;
        return this;
    }

    @Override
    public Date getViewedOn() {
        return viewedOn;
    }

    @Override
    public ViewedProduct viewedOn(Date viewedOn) {
        this.viewedOn = viewedOn;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.customerId = id_(map.get(Column.CUSTOMER_ID));
        this.productId = id_(map.get(Column.PRODUCT_ID));
        this.viewedOn = date_(map.get(Column.VIEWED_ON));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Column.ID, getId());
        map.put(Column.CUSTOMER_ID, getCustomerId());
        map.put(Column.PRODUCT_ID, getProductId());
        map.put(Column.VIEWED_ON, getViewedOn());

        return map;
    }
}
