package com.geecommerce.core.rest.service;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;

public interface RestService extends Service {
    public <T extends Model> T get(Class<T> modelClass, Id id);

    public <T extends Model> T get(Class<T> modelClass, Id id, QueryOptions queryOptions);

    public <T extends Model> List<T> get(Class<T> modelClass);

    public <T extends Model> List<T> get(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> List<T> get(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions);

    public <T extends Model> List<Id> getIds(Class<T> modelClass);

    public <T extends Model> List<Id> getIds(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> List<Id> getIds(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions);

    public <T extends Model> T create(T model);

    public <T extends Model> void update(T model);

    public <T extends Model> void remove(T model);

    public <T extends Model> void clearCaches(Class<T> modelClass);

    public <T extends Model> T getSnapshot(Class<T> modelClass, Id id, Integer version);

    public <T extends Model> T getSnapshot(Class<T> modelClass, Id id, Integer version, QueryOptions queryOptions);

    public <T extends Model> List<T> getSnapshots(Class<T> modelClass, Id id);

    public <T extends Model> List<T> getSnapshots(Class<T> modelClass, Id id, QueryOptions queryOptions);

    public <T extends Model> List<T> getSnapshots(Class<T> modelClass, Id id, Integer[] versions);

    public <T extends Model> List<T> getSnapshots(Class<T> modelClass, Id id, Integer[] versions, QueryOptions queryOptions);

    public <T extends Model> List<T> getSnapshots(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> List<T> getSnapshots(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions);
}
