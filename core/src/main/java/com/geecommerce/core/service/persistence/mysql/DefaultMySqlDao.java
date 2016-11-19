package com.geecommerce.core.service.persistence.mysql;

import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.db.annotation.Persistence;
import com.geecommerce.core.service.Annotations;
import com.geecommerce.core.service.annotation.Dao;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.persistence.jdbc.AbstractSqlDao;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@Dao
@Persistence("mysql")
public class DefaultMySqlDao extends AbstractSqlDao implements MySqlDao {

    @Inject
    public DefaultMySqlDao(Connections connections, CacheManager cacheManager) {
        super(connections, cacheManager);
    }

    @Override
    protected <T extends Model> String getTableName(Class<T> modelClass) {
        return Annotations.getCollectionName(modelClass);
    }
}
