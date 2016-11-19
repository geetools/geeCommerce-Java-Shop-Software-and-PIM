package com.geecommerce.mediaassets.dao;

import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.service.Annotations;
import com.geecommerce.core.service.annotation.Dao;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.persistence.mongodb.AbstractMongoDao;
import com.google.inject.Inject;

@Dao
public class DefaultMediaAssetDao extends AbstractMongoDao implements MediaAssetDao {
    private static final String COLLECTION_NAME = "media_assets";

    @Inject
    public DefaultMediaAssetDao(Connections connections, CacheManager cacheManager) {
        super(connections, cacheManager);
    }

    @Override
    protected <T extends Model> String getCollectionName(Class<T> modelClass) {
        return modelClass == null ? COLLECTION_NAME : Annotations.getCollectionName(modelClass);
    }
}
