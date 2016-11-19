package com.geecommerce.core.system.inject;

import com.geecommerce.core.service.persistence.postgres.DefaultPostgresDao;
import com.geecommerce.core.service.persistence.postgres.PostgresDao;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class PostgresModule extends AbstractModule {
    @Override
    protected void configure() {
        super.bind(PostgresDao.class).to(DefaultPostgresDao.class).in(Singleton.class);
    }
}