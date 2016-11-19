package com.geecommerce.core.service;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface AttributeSupport extends MultiContextModel {
    public AttributeTargetObject targetObject();

    public Attribute getAttributeDefinition(Id attributeId);

    public Attribute getAttributeDefinition(String attributeCode);

    public List<AttributeValue> getAttributes();

    public void setAttributes(List<AttributeValue> attributes);

    public AttributeSupport addAttribute(AttributeValue attribute);

    public AttributeSupport addAttribute(String attributeCode, ContextObject<?> value);

    public AttributeSupport addAttribute(String attributeCode, Object value);

    public AttributeSupport addAttribute(String attributeCode, String language, String value);

    public AttributeSupport addAttribute(String attributeCode, Object value, Store store);

    public AttributeSupport addAttribute(String attributeCode, Id optionId);

    public AttributeSupport addAttribute(String attributeCode, List<Id> optionIds);

    public AttributeSupport addXOptionAttribute(String attributeCode, ContextObject<List<Id>> xOptionIds);

    public AttributeSupport addAttributeUsingCode2(String attributeCode2, Object value);

    public AttributeSupport addAttributeUsingCode2(String attributeCode2, Id optionId);

    public AttributeSupport setAttribute(String attributeCode, Object value);

    public AttributeSupport setAttribute(String attributeCode, String language, String value);

    public AttributeSupport setAttribute(String attributeCode, Object value, Store store);

    public AttributeSupport setAttribute(String attributeCode, Id optionId);

    public AttributeSupport setAttribute(String attributeCode, List<Id> optionIds);

    public boolean hasAttribute(Id attributeId);

    public boolean hasAttribute(String attributeCode);

    public boolean hasAttributeValue(String attributeCode, String language);

    public boolean isAttributeOptedOut(Id attributeId);

    public boolean isAttributeOptedOut(String attributeCode);

    public boolean isAttributeEmpty(Id attributeId);

    public boolean isAttributeEmpty(Id attributeId, Store store);

    public boolean isAttributeEmpty(String attributeCode);

    public boolean isAttributeEmpty(String attributeCode, Store store);

    public AttributeValue attr(Id attributeId);

    public AttributeValue getAttribute(Id attributeId);

    public AttributeValue attr(String attributeCode);

    public AttributeValue getAttribute(String attributeCode);

    public AttributeValue attr(String attributeCode, boolean allowParentLookup);

    public AttributeValue getAttribute(String attributeCode, boolean allowParentLookup);

    public AttributeValue attr(String attributeCode, boolean allowParentLookup, ChildSupport.Lookup allowChildLookup);

    public AttributeValue getAttribute(String attributeCode, boolean allowParentLookup, ChildSupport.Lookup allowChildLookup);

    public AttributeValue getAttribute(Id attributeId, boolean allowParentLookup);

    public AttributeValue attr(Id attributeId, boolean allowParentLookup, ChildSupport.Lookup allowChildLookup);

    public AttributeValue getAttribute(Id attributeId, boolean allowParentLookup, ChildSupport.Lookup allowChildLookup);

    public boolean attributeValueEquals(String attributeCode, Object value);

    public boolean attributeValueEquals(String attributeCode, Object value, boolean allowParentLookup);

    public boolean attributeValueEquals(String attributeCode, Object value, boolean allowParentLookup, ChildSupport.Lookup allowChildLookup);

    public AttributeSupport setAttributes(Map<String, ContextObject<?>> attributes);

    public AttributeSupport setOptionAttributes(Map<String, List<Id>> attributesMap);

    public AttributeSupport setXOptionAttributes(Map<String, ContextObject<List<Id>>> attributesMap);

    public AttributeSupport addXOptionAttribute(String attributeCode, Id optionId, Store store);

    public AttributeSupport addXOptionAttribute(String attributeCode, List<Id> optionIds, Store store);

    public AttributeSupport setXOptionAttribute(String attributeCode, Id optionId, Store store);

    public AttributeSupport setXOptionAttribute(String attributeCode, List<Id> optionIds, Store store);

    public AttributeSupport setOptOuts(Map<String, ContextObject<Boolean>> optOutMap);

    public AttributeSupport setOptOut(String attributeCode, ContextObject<Boolean> optOut);

    public boolean hasAttributeWithOption(Id attributeId, Id attributeOptionId);

    public boolean hasAttributeWithOption(String attributeCode, Id attributeOptionId);

    public AttributeValue getAttributeHavingOption(Id attributeId, Id attributeOptionId);

    public AttributeValue getAttributeHavingOption(String attributeCode, Id attributeOptionId);

    public List<AttributeValue> getAttributesHavingProperty(String key);

    public List<AttributeValue> getAttributesHavingPrefix(String attributeCodePrefix);

    public AttributeSupport removeAttribute(Id attributeId);

    public AttributeSupport removeAttribute(String attributeCode);

    static final class AttributeSupportColumn {
        public static final String ATTRIBUTES = "attributes";
    }
}
