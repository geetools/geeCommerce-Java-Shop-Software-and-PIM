package com.geecommerce.customerReview.dao;

import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.service.annotation.Dao;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.persistence.mongodb.AbstractMongoDao;
import com.google.inject.Inject;

@Dao
public class DefaultCustomerReviewDao extends AbstractMongoDao implements CustomerReviewDao {

    private static final String COLLECTION_NAME = "customer_reviews";

    @Inject
    public DefaultCustomerReviewDao(Connections connections, CacheManager cacheManager) {
        super(connections, cacheManager);
    }

    @Override
    protected <T extends Model> String getCollectionName(Class<T> modelClass) {
        return COLLECTION_NAME;
    }
}
