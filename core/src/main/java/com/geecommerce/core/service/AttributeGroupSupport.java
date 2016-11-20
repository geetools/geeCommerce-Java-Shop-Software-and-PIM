package com.geecommerce.core.service;

import java.util.List;

import com.geecommerce.core.system.attribute.model.AttributeValue;

public interface AttributeGroupSupport extends AttributeSupport {
    List<AttributeValue> getAttributes(String code);
}
