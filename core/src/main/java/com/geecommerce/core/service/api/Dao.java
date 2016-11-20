package com.geecommerce.core.service.api;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.type.Id;
import com.mongodb.DBCollection;

public interface Dao {
    public <T extends Model> T findById(Class<T> modelClass, Id id);

    public <T extends Model> T findById(Class<T> modelClass, Id id, QueryOptions queryOptions);

    public <T extends Model> List<T> findByIds(Class<T> modelClass, Id[] ids);

    public <T extends Model> List<T> findByIds(Class<T> modelClass, Id[] ids, QueryOptions queryOptions);

    public <T extends Model> List<T> findAll(Class<T> modelClass);

    public <T extends Model> List<T> findAll(Class<T> modelClass, QueryOptions queryOptions);

    public <T extends Model> List<T> find(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> List<T> find(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions);

    public <T extends Model> T findOne(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> List<Id> findIds(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> List<Id> findIds(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions);

    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass);

    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions);

    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions, DBCollection collection);

    public <T extends Model> List<Map<String, Object>> findDataByIds(Class<T> modelClass, Id[] ids);

    public <T extends Model> List<Map<String, Object>> findDataByIds(Class<T> modelClass, Id[] ids,
        QueryOptions queryOptions);

    public <T extends Model> List<Object> distinct(Class<T> modelClass, String... distinctField);

    public <T extends Model> List<Object> distinct(Class<T> modelClass, Map<String, Object> filter,
        String... distinctField);

    public <T extends Model> List<Object> distinct(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions, String... distinctField);

    public <T extends Model> Long count(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> Long count(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions);

    public <T extends Model> T create(T entity);

    public <T extends Model> void update(T entity);

    public <T extends Model> void update(T entity, Map<String, Object> filter);

    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert);

    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert, boolean multi);

    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert, boolean multi,
        String... updateFields);

    public <T extends Model> void delete(T entity);

    public <T extends Model> void delete(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> void clearCaches(Class<T> modelClass);

    public void createDatabase(String name);

    public <T extends Model> T findSnapshot(Class<T> modelClass, Id id, Integer version);

    public <T extends Model> T findSnapshot(Class<T> modelClass, Id id, Integer version, QueryOptions queryOptions);

    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id);

    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, QueryOptions queryOptions);

    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, Integer[] versions);

    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, Integer[] versions,
        QueryOptions queryOptions);

    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions);

    public <T extends Model> Long snapshotCount(Class<T> modelClass, Map<String, Object> filter);
}
