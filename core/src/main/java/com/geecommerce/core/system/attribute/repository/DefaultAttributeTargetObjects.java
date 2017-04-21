package com.geecommerce.core.system.attribute.repository;

import java.util.LinkedHashMap;
import java.util.Map;

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
        filter.put(AttributeTargetObject.Col.TYPE, modelInterfaceFQN.getName());

        return findOne(AttributeTargetObject.class, filter);
    }
}
