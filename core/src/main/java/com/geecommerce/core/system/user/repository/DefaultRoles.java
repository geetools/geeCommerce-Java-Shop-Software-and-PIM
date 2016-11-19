package com.geecommerce.core.system.user.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.core.system.user.model.Role;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DefaultRoles extends AbstractRepository implements Roles {
    private final MongoDao mongoDao;

    @Inject
    public DefaultRoles(MongoDao mongoDao) {
        this.mongoDao = mongoDao;
    }

    @Override
    public Dao dao() {
        return this.mongoDao;
    }

    @Override
    public List<Role> findExceptIds(List<Id> ids) {
        DBObject inClause = new BasicDBObject();
        inClause.put("$in", ids);
        DBObject notInClause = new BasicDBObject();
        notInClause.put("$not", inClause);

        Map<String, Object> filter = new HashMap<>();
        filter.put(Role.Column.ID, notInClause);

        return mongoDao.find(Role.class, filter);
    }
}
