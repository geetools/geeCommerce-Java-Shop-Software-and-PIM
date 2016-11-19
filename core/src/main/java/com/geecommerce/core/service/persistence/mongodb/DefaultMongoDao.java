package com.geecommerce.core.service.persistence.mongodb;

import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.db.annotation.Persistence;
import com.geecommerce.core.service.Annotations;
import com.geecommerce.core.service.annotation.Dao;
import com.geecommerce.core.service.api.Model;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@Dao
@Persistence("mongodb")
public class DefaultMongoDao extends AbstractMongoDao implements MongoDao {
    @Inject
    public DefaultMongoDao(Connections connections, CacheManager cacheManager) {
        super(connections, cacheManager);
    }

    @Override
    protected <T extends Model> String getCollectionName(Class<T> modelClass) {
        return Annotations.getCollectionName(modelClass);
    }
}
