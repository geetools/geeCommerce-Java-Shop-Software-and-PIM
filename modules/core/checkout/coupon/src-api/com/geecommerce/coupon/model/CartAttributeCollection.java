package com.geecommerce.coupon.model;

import java.util.Map;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.type.Id;

public interface CartAttributeCollection extends Model {

    public Map<String, AttributeValue> getCartAttributes();

    public CartAttributeCollection setCartAttributes(Map<String, AttributeValue> cartAttributes);

    public Map<Id, Map<String, AttributeValue>> getCartItemAttributes();

    public CartAttributeCollection setCartItemAttributes(Map<Id, Map<String, AttributeValue>> cartItemAttributes);

    public Map<Id, Map<String, AttributeValue>> getProductAttributes();

    public CartAttributeCollection setProductAttributes(Map<Id, Map<String, AttributeValue>> productAttributes);
}
