package com.geecommerce.core.service;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.service.api.Service;
import com.google.inject.Inject;

public abstract class AbstractService implements Service {
    @Inject
    protected App app;

    public <T extends Model> List<T> find(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions) {
        Repository repos = app.inject(Repository.class);

        return repos.find(modelClass, filter, queryOptions);
    }
}
