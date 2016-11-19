package com.geecommerce.shipping.model;

import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.shipping.enums.ShippingType;

@Model
public class DefaultShippingPackage extends AbstractModel implements ShippingPackage {
    private static final long serialVersionUID = 6687373142353311542L;
    private ShippingAddress shippingAddress;
    private List<ShippingItem> shippingItems;
    private Double totalAmount = 0.0;
    private ShippingType type;
    private boolean calculateShipping = true;

    @Override
    public ShippingAddress getShippingAddress() {
	return shippingAddress;
    }

    @Override
    public ShippingPackage setShippingAddress(ShippingAddress shippingAddress) {
	this.shippingAddress = shippingAddress;
	return this;
    }

    @Override
    public List<ShippingItem> getShippingItems() {
	return shippingItems;
    }

    @Override
    public ShippingPackage setShippingItems(List<ShippingItem> shippingItems) {
	this.shippingItems = shippingItems;
	return this;
    }

    @Override
    public ShippingPackage addShippingItem(ShippingItem shippingItem) {
	if (shippingItems == null)
	    shippingItems = new ArrayList<>();
	shippingItems.add(shippingItem);
	return this;
    }

    @Override
    public Double getTotalAmount() {
	return totalAmount;
    }

    @Override
    public ShippingPackage setTotalAmount(Double totalAmount) {
	this.totalAmount = totalAmount;
	return this;
    }

    @Override
    public ShippingType getType() {
	return type;
    }

    @Override
    public ShippingPackage setType(ShippingType type) {
	this.type = type;
	return this;
    }

    @Override
    public ShippingPackage setCalculateShipping(boolean calculateShipping) {
	this.calculateShipping = calculateShipping;
	return this;
    }

    @Override
    public boolean getCalculateShipping() {
	return calculateShipping;
    }

    @Override
    public Id getId() {
	return null;
    }
}
