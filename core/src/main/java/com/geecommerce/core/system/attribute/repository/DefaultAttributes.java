package com.geecommerce.core.system.attribute.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.enums.InputType;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.google.inject.Inject;
import com.mongodb.QueryOperators;

public class DefaultAttributes extends AbstractRepository implements Attributes {
    @Inject
    protected App app;

    protected final MongoDao mongoDao;
    protected final AttributeTargetObjects attributeTargetObjects;

    @Inject
    public DefaultAttributes(MongoDao mongoDao, AttributeTargetObjects attributeTargetObjects) {
        this.mongoDao = mongoDao;
        this.attributeTargetObjects = attributeTargetObjects;
    }

    @Override
    public Dao dao() {
        return this.mongoDao;
    }

    @Override
    public List<Attribute> thatBelongTo(AttributeTargetObject targetObject, QueryOptions queryOptions) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put(Attribute.Col.ENABLED, true);

        if (targetObject != null) {
            filter.put(Attribute.Col.TARGET_OBJECT_ID, targetObject.getId());
        }

        return app.isAPIRequest() ? find(Attribute.class, filter, queryOptions)
            : multiContextFind(Attribute.class, filter, Attribute.Col.CODE, queryOptions);
    }

    @Override
    public List<Attribute> thatAreMandatory(AttributeTargetObject targetObject, QueryOptions queryOptions) {
        return thatAreMandatory(targetObject, queryOptions, false);
    }

    @Override
    public List<Attribute> thatAreMandatory(AttributeTargetObject targetObject, QueryOptions queryOptions,
        boolean includeOptOutAttributes) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put(Attribute.Col.ENABLED, true);

        if (includeOptOutAttributes) {
            Map<String, int[]> in = new LinkedHashMap<>();
            in.put(QueryOperators.IN, new int[] { InputType.MANDATORY.toId(), InputType.OPTOUT.toId() });

            filter.put(Attribute.Col.INPUT_TYPE, in);
        } else {
            filter.put(Attribute.Col.INPUT_TYPE, InputType.MANDATORY.toId());
        }

        if (targetObject != null) {
            filter.put(Attribute.Col.TARGET_OBJECT_ID, targetObject.getId());
        }

        return multiContextFind(Attribute.class, filter, Attribute.Col.CODE);
    }

    @Override
    public List<Attribute> thatAreMandatoryAndEditable(AttributeTargetObject targetObject) {
        return thatAreMandatoryAndEditable(targetObject, null);
    }

    @Override
    public List<Attribute> thatAreMandatoryAndEditable(AttributeTargetObject targetObject,
        boolean includeOptOutAttributes) {
        return thatAreMandatoryAndEditable(targetObject, null, includeOptOutAttributes);
    }

    @Override
    public List<Attribute> thatAreMandatoryAndEditable(AttributeTargetObject targetObject, QueryOptions queryOptions) {
        return thatAreMandatoryAndEditable(targetObject, queryOptions, false);
    }

    @Override
    public List<Attribute> thatAreMandatoryAndEditable(AttributeTargetObject targetObject, QueryOptions queryOptions,
        boolean includeOptOutAttributes) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put(Attribute.Col.ENABLED, true);
        filter.put(Attribute.Col.EDITABLE, true);

        if (includeOptOutAttributes) {
            Map<String, int[]> in = new LinkedHashMap<>();
            in.put(QueryOperators.IN, new int[] { InputType.MANDATORY.toId(), InputType.OPTOUT.toId() });

            filter.put(Attribute.Col.INPUT_TYPE, in);
        } else {
            filter.put(Attribute.Col.INPUT_TYPE, InputType.MANDATORY.toId());
        }

        if (targetObject != null) {
            filter.put(Attribute.Col.TARGET_OBJECT_ID, targetObject.getId());
        }

        return multiContextFind(Attribute.class, filter, Attribute.Col.CODE, queryOptions);
    }

    @Override
    public Attribute havingCode(AttributeTargetObject targetObject, String code) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put(Attribute.Col.CODE, code);
        filter.put(Attribute.Col.ENABLED, true);

        if (targetObject != null) {
            filter.put(Attribute.Col.TARGET_OBJECT_ID, targetObject.getId());
        }

        return multiContextFindOne(Attribute.class, filter);
    }

    @Override
    public Attribute havingCode2(AttributeTargetObject targetObject, String code2) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put(Attribute.Col.CODE2, code2);
        filter.put(Attribute.Col.ENABLED, true);

        if (targetObject != null) {
            filter.put(Attribute.Col.TARGET_OBJECT_ID, targetObject.getId());
        }

        return multiContextFindOne(Attribute.class, filter);
    }

    @Override
    public List<Attribute> forSearchFilter(List<AttributeTargetObject> targetObjects) {
        List<Map<String, Object>> filters = new ArrayList<>();

        for (AttributeTargetObject targetObject : targetObjects) {
            Map<String, Object> filter = new LinkedHashMap<>();
            filter.put(Attribute.Col.ENABLED, true);
            filter.put(Attribute.Col.INCLUDE_IN_SEARCH_FILTER, true);
            filter.put(Attribute.Col.TARGET_OBJECT_ID, targetObject.getId());
            filters.add(filter);
        }

        Map<String, Object> orFilter = new LinkedHashMap<>();
        orFilter.put(QueryOperators.OR, filters);

        return multiContextFind(Attribute.class, orFilter, Attribute.Col.CODE);
    }

    @Override
    public List<Attribute> forSearchFilter(String... targetObjectCodes) {
        List<Map<String, Object>> filters = new ArrayList<>();

        for (String targetObjectCode : targetObjectCodes) {

            AttributeTargetObject targetObject = attributeTargetObjects.havingCode(targetObjectCode);

            if (targetObject != null) {
                Map<String, Object> filter = new LinkedHashMap<>();
                filter.put(Attribute.Col.ENABLED, true);
                filter.put(Attribute.Col.INCLUDE_IN_SEARCH_FILTER, true);
                filter.put(Attribute.Col.TARGET_OBJECT_ID, targetObject.getId());
                filters.add(filter);
            }
        }

        Map<String, Object> orFilter = new LinkedHashMap<>();
        orFilter.put(QueryOperators.OR, filters);

        return multiContextFind(Attribute.class, orFilter, Attribute.Col.CODE);
    }

    @Override
    public List<Attribute> havingCodeBeginningWith(AttributeTargetObject targetObject, String codePrefix) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        Map<String, String> regex = new LinkedHashMap<String, String>();
        regex.put("$regex", new StringBuilder(Str.CARET).append(codePrefix).toString());

        filter.put(Attribute.Col.CODE, regex);
        filter.put(Attribute.Col.ENABLED, true);

        if (targetObject != null) {
            filter.put(Attribute.Col.TARGET_OBJECT_ID, targetObject.getId());
        }

        return multiContextFind(Attribute.class, filter, Attribute.Col.CODE);
    }

    @Override
    public List<Attribute> havingCode2BeginningWith(AttributeTargetObject targetObject, String code2Prefix) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        Map<String, String> regex = new LinkedHashMap<String, String>();
        regex.put("$regex", new StringBuilder(Str.CARET).append(code2Prefix).toString());

        filter.put(Attribute.Col.CODE2, regex);
        filter.put(Attribute.Col.ENABLED, true);

        if (targetObject != null) {
            filter.put(Attribute.Col.TARGET_OBJECT_ID, targetObject.getId());
        }

        return multiContextFind(Attribute.class, filter, Attribute.Col.CODE);
    }
}
