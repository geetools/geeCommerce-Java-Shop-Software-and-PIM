package com.geecommerce.core.system.attribute.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.system.attribute.repository.AttributeOptions;
import com.geecommerce.core.system.attribute.repository.AttributeTargetObjects;
import com.geecommerce.core.system.attribute.repository.Attributes;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;

public class DefaultAttributeService implements AttributeService {
    @Inject
    protected App app;

    protected final Attributes attributes;
    protected final AttributeOptions attributeOptions;
    protected final AttributeTargetObjects attributeTargetObjects;
    protected final Connections connections;

    @Inject
    public DefaultAttributeService(Attributes attributes, AttributeOptions attributeOptions,
        AttributeTargetObjects attributeTargetObjects, Connections connections) {
        this.attributes = attributes;
        this.attributeOptions = attributeOptions;
        this.attributeTargetObjects = attributeTargetObjects;
        this.connections = connections;
    }

    @Override
    public AttributeTargetObject getAttributeTargetObjectByCode(String targetObjectCode) {
        return attributeTargetObjects.havingCode(targetObjectCode);
    }

    @Override
    public AttributeTargetObject getAttributeTargetObject(Class<? extends AttributeSupport> modelInterface) {
        return getAttributeTargetObject(modelInterface, false);
    }

    @Override
    public AttributeTargetObject getAttributeTargetObject(Class<? extends AttributeSupport> modelInterface,
        boolean createIfNotExists) {
        AttributeTargetObject targetObject = attributeTargetObjects.forType(modelInterface);

        if (targetObject == null && createIfNotExists) {
            String code = StringUtils
                .join(StringUtils.splitByCharacterTypeCamelCase(modelInterface.getSimpleName()), Char.UNDERSCORE)
                .toLowerCase();
            String name = StringUtils.capitalize(StringUtils
                .join(StringUtils.splitByCharacterTypeCamelCase(modelInterface.getSimpleName()), Char.SPACE)
                .toLowerCase());

            targetObject = attributeTargetObjects.add(app.model(AttributeTargetObject.class)
                .setType(modelInterface.getName()).setCode(code).setName(ContextObjects.global(name)));
        }

        return targetObject;
    }

    @Override
    public Attribute createAttribute(Attribute attribute) {
        Attribute savedAttribute = attributes.add(attribute);

        if (attribute.getOptions() != null && attribute.getOptions().size() > 0) {
            for (AttributeOption option : attribute.getOptions()) {
                // Make sure that the option knows the attribute-id
                option.belongsTo(savedAttribute);
                attributeOptions.add(option);
            }
        }

        return savedAttribute;
    }

    @Override
    public AttributeOption createAttributeOption(AttributeOption attributeOption) {
        return attributeOptions.add(attributeOption);
    }

    @Override
    public void updateAttribute(Attribute attribute) {
        attributes.update(attribute);
    }

    @Override
    public Attribute getAttribute(Id id) {
        return attributes.findById(Attribute.class, id);
    }

    @Override
    public Attribute getAttribute(Id attrTargetObjectId, String code) {
        return getAttribute(attributeTargetObjects.findById(AttributeTargetObject.class, attrTargetObjectId), code);
    }

    @Override
    public Attribute getAttribute(AttributeTargetObject attrTargetObject, String code) {
        return attributes.havingCode(attrTargetObject, code);
    }

    @Override
    public Map<Id, Map<String, String>> getAttributeCodesBeginningWith(Id attrTargetObjectId, String codePrefix) {
        Map<Id, Map<String, String>> attributeCodes = new HashMap<>();

        List<Attribute> attributeList = attributes.havingCodeBeginningWith(
            attributeTargetObjects.findById(AttributeTargetObject.class, attrTargetObjectId), codePrefix);

        for (Attribute attribute : attributeList) {
            Map<String, String> m = new HashMap<>();
            m.put(Attribute.Col.CODE, attribute.getCode());
            m.put(Attribute.Col.CODE2, attribute.getCode2());

            attributeCodes.put(attribute.getId(), m);
        }

        return attributeCodes;
    }

    @Override
    public Map<Id, Map<String, String>> getAttributeCodesBeginningWithCode2Prefix(Id attrTargetObjectId,
        String code2Prefix) {
        Map<Id, Map<String, String>> attributeCodes = new HashMap<>();

        List<Attribute> attributeList = attributes.havingCode2BeginningWith(
            attributeTargetObjects.findById(AttributeTargetObject.class, attrTargetObjectId), code2Prefix);

        for (Attribute attribute : attributeList) {
            Map<String, String> m = new HashMap<>();
            m.put(Attribute.Col.CODE, attribute.getCode());
            m.put(Attribute.Col.CODE2, attribute.getCode2());

            attributeCodes.put(attribute.getId(), m);
        }

        return attributeCodes;
    }

    @Override
    public List<Attribute> getAttributesFor(String targetObjectCode) {
        return getAttributesFor(targetObjectCode, null);
    }

    @Override
    public List<Attribute> getAttributesFor(String targetObjectCode, QueryOptions queryOptions) {
        return attributes.thatBelongTo(attributeTargetObjects.havingCode(targetObjectCode), queryOptions);
    }

    @Override
    public List<Attribute> getAttributesFor(Class<? extends AttributeSupport> targetObjectType) {
        return getAttributesFor(targetObjectType, null);
    }

    @Override
    public List<Attribute> getAttributesFor(Class<? extends AttributeSupport> targetObjectType,
        QueryOptions queryOptions) {
        return attributes.thatBelongTo(attributeTargetObjects.forType(targetObjectType), queryOptions);
    }

    @Override
    public List<Attribute> getAttributesFor(Id targetObjectId) {
        return getAttributesFor(targetObjectId, null);
    }

    @Override
    public List<Attribute> getAttributesFor(Id targetObjectId, QueryOptions queryOptions) {
        return attributes.thatBelongTo(attributeTargetObjects.findById(AttributeTargetObject.class, targetObjectId),
            queryOptions);
    }

    @Override
    public List<Attribute> getAttributes(Id[] ids) {
        return attributes.findByIds(Attribute.class, ids);
    }

    @Override
    public AttributeOption getAttributeOption(Id id) {
        return attributeOptions.findById(AttributeOption.class, id);
    }

    @Override
    public List<AttributeOption> getAttributeOptions(Id[] ids) {
        return attributeOptions.findByIds(AttributeOption.class, ids);
    }

    @Override
    public List<AttributeOption> getAttributeOptionsFor(List<Attribute> attributes) {
        return attributeOptions.thatBelongTo(attributes);
    }

    @Override
    public List<AttributeOption> createAttributeOptions(List<AttributeOption> options) {
        return attributeOptions.addAll(options);
    }

    @Override
    public void updateAttributeOption(AttributeOption option) {
        attributeOptions.update(option);
    }

    @Override
    public void updateAttributeOptions(List<AttributeOption> options) {
        attributeOptions.updateAll(options);
    }

    @Override
    public void deleteAttributeOption(Id id) {
        AttributeOption option = attributeOptions.findById(AttributeOption.class, id);

        if (option != null) {
            attributeOptions.remove(option);
        }
    }

    @Override
    public Map<String, Attribute> getAttributesForSearchFilter(List<Id> attrTargetObjectIds) {
        Map<String, Attribute> filterAttributes = new HashMap<>();

        List<Attribute> attributeList = attributes.forSearchFilter(attributeTargetObjects.findByIds(
            AttributeTargetObject.class, attrTargetObjectIds.toArray(new Id[attrTargetObjectIds.size()])));

        for (Attribute attribute : attributeList) {
            filterAttributes.put(attribute.getCode(), attribute);
        }

        return filterAttributes;
    }

    @Override
    public Map<String, Attribute> getAttributesForSearchFilter(String... attrTargetObjectCodes) {
        Map<String, Attribute> filterAttributes = new HashMap<>();

        List<Attribute> attributeList = attributes.forSearchFilter(attrTargetObjectCodes);

        for (Attribute attribute : attributeList) {
            filterAttributes.put(attribute.getCode(), attribute);
        }

        return filterAttributes;
    }

    @Override
    public List<String> getOptionTags(Id attributeId) {
        return attributeOptions.findOptionTags(attributeId);
    }

    // TODO:find better place for that
    @Override
    public List<String> getSuggestions(Id attributeId, String collectionName, String lang, String query) {
        DBObject val = new BasicDBObject("$elemMatch",
            new BasicDBObject("val", new BasicDBObject("$regex", "^" + query + ".*")));

        DBObject elemMathContent = new BasicDBObject("attr_id", attributeId);
        elemMathContent.put("val", val);

        DBObject elemMatch = new BasicDBObject("$elemMatch", elemMathContent);

        List<DBObject> alls = new ArrayList<>();
        alls.add(elemMatch);

        DBObject match = new BasicDBObject("$match", new BasicDBObject("attributes", new BasicDBObject("$all", alls)));

        DBObject matchAdditionalContent = new BasicDBObject("attributes.attr_id", attributeId);
        if (lang != null && !lang.isEmpty()) {
            matchAdditionalContent.put("attributes.val.l", lang);
        }
        matchAdditionalContent.put("attributes.val.val", new BasicDBObject("$regex", "^" + query + ".*"));

        DBObject matchAdditional = new BasicDBObject("$match", matchAdditionalContent);
        AggregationOutput output = ((DB) connections.getFirstConnection("mongodb")).getCollection(collectionName)
            .aggregate(match, new BasicDBObject("$unwind", "$attributes"),
                new BasicDBObject("$unwind", "$attributes.val"), matchAdditional,
                new BasicDBObject("$group", new BasicDBObject("_id", "$attributes.val.val")),
                new BasicDBObject("$sort", new BasicDBObject("_id", 1)));

        List<String> suggestions = new ArrayList<>();

        for (DBObject result : output.results()) {
            suggestions.add((String) result.get("_id"));
        }

        return suggestions;
    }
}
