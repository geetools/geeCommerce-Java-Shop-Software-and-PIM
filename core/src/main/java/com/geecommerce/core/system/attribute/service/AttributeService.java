package com.geecommerce.core.system.attribute.service;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.system.attribute.pojo.BatchData;
import com.geecommerce.core.type.Id;

public interface AttributeService extends Service {
    public AttributeTargetObject getAttributeTargetObjectByCode(String targetObjectCode);

    public AttributeTargetObject getAttributeTargetObject(Class<? extends AttributeSupport> modelInterface);

    public AttributeTargetObject getAttributeTargetObject(Class<? extends AttributeSupport> modelInterface,
        boolean createIfNotExists);

    public Attribute createAttribute(Attribute attribute);

    public AttributeOption createAttributeOption(AttributeOption attributeOption);

    public List<AttributeOption> createAttributeOptions(List<AttributeOption> options);

    public void refreshAttribute(Id id);
    
    public Attribute getAttribute(Id id);

    public Attribute getAttribute(Id attrTargetObjectId, String code);

    public Attribute getAttribute(AttributeTargetObject attrTargetObject, String code);

    public List<Attribute> getAttributesFor(Class<? extends AttributeSupport> targetObjectType);

    public List<Attribute> getAttributesFor(Class<? extends AttributeSupport> targetObjectType,
        QueryOptions queryOptions);

    public Map<Id, Map<String, String>> getAttributeCodesBeginningWith(Id attrTargetObjectId, String codePrefix);

    public Map<Id, Map<String, String>> getAttributeCodesBeginningWithCode2Prefix(Id attrTargetObjectId,
        String code2Prefix);

    public Map<String, Attribute> getAttributesForSearchFilter(List<Id> attrTargetObjectIds);

    public Map<String, Attribute> getAttributesForSearchFilter(String... attrTargetObjectCodes);

    public List<Attribute> getAttributesFor(String targetObjectCode);

    public List<Attribute> getAttributesFor(String targetObjectCode, QueryOptions queryOptions);

    public List<Attribute> getAttributesFor(Id targetObjectId);

    public List<Attribute> getAttributesFor(Id targetObjectId, QueryOptions queryOptions);

    public List<Attribute> getAttributes(Id[] ids);

    public AttributeOption getAttributeOption(Id id);

    public List<AttributeOption> getAttributeOptions(Id[] ids);

    public List<AttributeOption> getAttributeOptionsFor(List<Attribute> attributes);

    public void updateAttribute(Attribute attribute);

    public void updateAttributeOption(AttributeOption option);

    public void updateAttributeOptions(List<AttributeOption> attributeOptions);

    public void deleteAttributeOption(Id id);

    public List<String> getOptionTags(Id id);

    public List<String> getSuggestions(Id attributeId, String collectionName, String lang, String query);

    void processBatchUpdate(BatchData batchData);
}
