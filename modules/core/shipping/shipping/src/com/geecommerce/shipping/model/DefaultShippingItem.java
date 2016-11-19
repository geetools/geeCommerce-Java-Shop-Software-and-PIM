package com.geecommerce.shipping.model;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model
public class DefaultShippingItem extends AbstractModel implements ShippingItem {
    private static final long serialVersionUID = 6243318596433849536L;
    private Integer quantity = null;
    private Double width = null;
    private Double height = null;
    private Double depth = null;
    private Double weight = null;
    private Double price = null;
    private Id productId = null;
    private String productName = null;
    private String pickupStoreId = null;

    @Override
    public String getProductName() {
	return productName;
    }

    @Override
    public ShippingItem setProductName(String productName) {
	this.productName = productName;
	return this;
    }

    @Override
    public Id getProductId() {
	return productId;
    }

    @Override
    public ShippingItem setProductId(Id productId) {
	this.productId = productId;
	return this;
    }

    @Override
    public Integer getQuantity() {
	return quantity;
    }

    @Override
    public ShippingItem setQuantity(Integer quantity) {
	this.quantity = quantity;
	return this;
    }

    @Override
    public Double getWidth() {
	return width;
    }

    @Override
    public ShippingItem setWidth(Double width) {
	this.width = width;
	return this;
    }

    @Override
    public Double getHeight() {
	return height;
    }

    @Override
    public ShippingItem setHeight(Double height) {
	this.height = height;
	return this;
    }

    @Override
    public Double getDepth() {
	return depth;
    }

    @Override
    public ShippingItem setDepth(Double depth) {
	this.depth = depth;
	return this;
    }

    @Override
    public Double getWeight() {
	return weight;
    }

    @Override
    public ShippingItem setWeight(Double weight) {
	this.weight = weight;
	return this;
    }

    @Override
    public Double getPrice() {
	return price;
    }

    @Override
    public ShippingItem setPrice(Double price) {
	this.price = price;
	return this;
    }

    @Override
    public String getPickupStoreId() {
	return pickupStoreId;
    }

    @Override
    public ShippingItem setPickupStoreId(String pickupStoreId) {
	this.pickupStoreId = pickupStoreId;
	return this;
    }

    @Override
    public Id getId() {
	return null;
    }
}
