package com.geecommerce.news.subscription.repository;

import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.Id;
import com.geecommerce.news.subscription.model.NewsSubscriber;
import com.google.inject.Inject;
import com.mongodb.QueryOperators;

@Repository
public class DefaultNewsSubscribers extends AbstractRepository implements NewsSubscribers {
    private final MongoDao mongoDao;

    @Inject
    public DefaultNewsSubscribers(MongoDao mongoDao) {
        this.mongoDao = mongoDao;
    }

    @Override
    public Dao dao() {
        return this.mongoDao;
    }

    @Override
    public NewsSubscriber thatBelongTo(String email, Store store) {
        Map<String, Object> in = new HashMap<>();
        in.put(QueryOperators.IN, new Id[] { store.getId() });

        Map<String, Object> filter = new HashMap<>();
        filter.put(NewsSubscriber.Col.EMAIL, email);
        filter.put(GlobalColumn.STORE_ID, in);

        return findOne(NewsSubscriber.class, filter);
    }
}
