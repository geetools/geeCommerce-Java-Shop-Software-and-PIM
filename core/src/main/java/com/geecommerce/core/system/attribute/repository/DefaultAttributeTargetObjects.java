package com.geecommerce.core.system.attribute.repository;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.google.inject.Inject;

public class DefaultAttributeTargetObjects extends AbstractRepository implements AttributeTargetObjects {
    final MongoDao mongoDao;

    @Inject
    public DefaultAttributeTargetObjects(MongoDao mongoDao) {
        this.mongoDao = mongoDao;
    }

    @Override
    public Dao dao() {
        return this.mongoDao;
    }

    @Override
    public AttributeTargetObject havingCode(String targetObjectCode) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put(AttributeTargetObject.Col.CODE, targetObjectCode);

        return findOne(AttributeTargetObject.class, filter);
    }

    @Override
    public AttributeTargetObject forType(Class<? extends AttributeSupport> modelInterfaceFQN) {
        Map<String, Object> filter = new LinkedHashMap<>();

        Set<String> types = new HashSet<>();
        types.add(modelInterfaceFQN.getName());

        Map<String, Object> attrInFilter = new LinkedHashMap<>();
        attrInFilter.put("$in", types);
        filter.put(AttributeTargetObject.Col.TYPES, attrInFilter);

        return findOne(AttributeTargetObject.class, filter);
    }
}
