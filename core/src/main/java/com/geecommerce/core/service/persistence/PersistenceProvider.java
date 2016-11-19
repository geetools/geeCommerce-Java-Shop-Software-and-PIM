package com.geecommerce.core.service.persistence;

import com.geecommerce.core.service.RepositorySupport;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.api.Model;

public interface PersistenceProvider {
    RepositorySupport provideRepositorySupport(Class<? extends Model> modelClass, Dao dao);

    Dao provideDao(Class<? extends Model> modelClass);

    boolean isCompatible(Class<?> clazz);
}
