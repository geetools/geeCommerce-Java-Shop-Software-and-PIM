package com.geecommerce.core.system.inject;

import com.geecommerce.core.service.persistence.mongodb.DefaultMongoDao;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class MongoModule extends AbstractModule {
    @Override
    protected void configure() {
        super.bind(MongoDao.class).to(DefaultMongoDao.class).in(Singleton.class);
    }
}