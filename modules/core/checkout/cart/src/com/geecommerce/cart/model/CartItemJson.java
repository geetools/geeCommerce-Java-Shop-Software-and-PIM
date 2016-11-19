package com.geecommerce.cart.model;

import com.geecommerce.core.type.Id;

public class CartItemJson {

    private Id productId = null;
    private Integer quantity = null;
    private String deliveryMethod = null;
    private Id variant = null;

    public Id getProductId() {
	return productId;
    }

    public void setProductId(Id productId) {
	this.productId = productId;
    }

    public Integer getQuantity() {
	return quantity;
    }

    public void setQuantity(Integer quantity) {
	this.quantity = quantity;
    }

    public Id getVariant() {
	return variant;
    }

    public void setVariant(Id variant) {
	this.variant = variant;
    }

    public String getDeliveryMethod() {
	return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
	this.deliveryMethod = deliveryMethod;
    }
}
