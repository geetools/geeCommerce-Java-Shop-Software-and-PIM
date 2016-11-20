package com.geecommerce.core.proxy;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.persistence.jdbc.SqlDao;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

public class ProxyDao {
    private final MongoDao mongoDao;

    private final SqlDao sqlDao;

    @Inject
    public ProxyDao(MongoDao mongoDao, SqlDao sqlDao) {
        this.mongoDao = mongoDao;
        this.sqlDao = sqlDao;
    }

    public <T extends Model> T mFindById(Class<T> modelClass, Id id) {
        return mongoDao.findById(modelClass, id);
    }

    public <T extends Model> T sFindById(Class<T> modelClass, Id id) {
        return sqlDao.findById(modelClass, id);
    }
}
