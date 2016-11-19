package com.geecommerce.core.system.inject;

import com.geecommerce.core.service.persistence.mysql.DefaultMySqlDao;
import com.geecommerce.core.service.persistence.mysql.MySqlDao;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class MySqlModule extends AbstractModule {
    @Override
    protected void configure() {
        super.bind(MySqlDao.class).to(DefaultMySqlDao.class).in(Singleton.class);
    }
}