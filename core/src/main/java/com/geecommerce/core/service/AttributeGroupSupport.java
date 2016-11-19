package com.geecommerce.core.service;

import com.geecommerce.core.system.attribute.model.AttributeValue;

import java.util.List;

public interface AttributeGroupSupport extends AttributeSupport {
    List<AttributeValue> getAttributes(String code);
}
