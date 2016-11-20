package com.geecommerce.coupon.model;

import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.type.Id;

@Model
public class DefaultCartAttributeCollection extends AbstractModel implements CartAttributeCollection {

    private Map<String, AttributeValue> cartAttributes = null;
    private Map<Id, Map<String, AttributeValue>> cartItemAttributes = null;
    private Map<Id, Map<String, AttributeValue>> productAttributes = null;

    @Override
    public Map<String, AttributeValue> getCartAttributes() {
        return cartAttributes;
    }

    @Override
    public CartAttributeCollection setCartAttributes(Map<String, AttributeValue> cartAttributes) {
        this.cartAttributes = cartAttributes;
        return this;
    }

    @Override
    public Map<Id, Map<String, AttributeValue>> getCartItemAttributes() {
        return cartItemAttributes;
    }

    @Override
    public CartAttributeCollection setCartItemAttributes(Map<Id, Map<String, AttributeValue>> cartItemAttributes) {
        this.cartItemAttributes = cartItemAttributes;
        return this;
    }

    @Override
    public Map<Id, Map<String, AttributeValue>> getProductAttributes() {
        return productAttributes;
    }

    @Override
    public CartAttributeCollection setProductAttributes(Map<Id, Map<String, AttributeValue>> productAttributes) {
        this.productAttributes = productAttributes;
        return this;
    }

    @Override
    public Id getId() {
        return null;
    }
}
