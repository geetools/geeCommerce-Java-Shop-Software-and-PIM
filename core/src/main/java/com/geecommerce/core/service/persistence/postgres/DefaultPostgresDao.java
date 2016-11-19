package com.geecommerce.core.service.persistence.postgres;

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
@Persistence("postgres")
public class DefaultPostgresDao extends AbstractSqlDao implements PostgresDao {

    @Inject
    public DefaultPostgresDao(Connections connections, CacheManager cacheManager) {
        super(connections, cacheManager);
    }

    @Override
    protected <T extends Model> String getTableName(Class<T> modelClass) {
        return Annotations.getCollectionName(modelClass);
    }
}
