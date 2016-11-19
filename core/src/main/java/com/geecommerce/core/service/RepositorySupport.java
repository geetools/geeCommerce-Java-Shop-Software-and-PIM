package com.geecommerce.core.service;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.type.Id;

public interface RepositorySupport {
    public void dao(Dao dao);
    
    public <T extends Model> T findById(Class<T> modelClass, Id id);

    public <T extends Model> T findById(Class<T> modelClass, Id id, QueryOptions queryOptions);

    public <T extends Model> List<T> findByIds(Class<T> modelClass, Id[] ids);

    public <T extends Model> List<T> findByIds(Class<T> modelClass, Id[] ids, QueryOptions queryOptions);

    public <T extends Model> T findByUniqueKey(Class<T> modelClass, String key, Object value);

    public <T extends Model> List<T> findAll(Class<T> modelClass);

    public <T extends Model> List<T> findAll(Class<T> modelClass, QueryOptions queryOptions);

    public <T extends Model> List<T> find(Class<T> modelClass, String key, Object value);

    public <T extends Model> List<T> find(Class<T> modelClass, String key, Object value, QueryOptions queryOptions);

    public <T extends Model> List<T> find(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> List<T> find(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions);

    public <T extends Model> List<Id> findIds(Class<T> modelClass);

    public <T extends Model> List<Id> findIds(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> List<Id> findIds(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions);

    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass);

    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions);

    public <T extends Model> List<Map<String, Object>> findDataByIds(Class<T> modelClass, Id[] ids);

    public <T extends Model> List<Map<String, Object>> findDataByIds(Class<T> modelClass, Id[] ids, QueryOptions queryOptions);

    public <T extends Model> List<?> distinct(Class<T> modelClass, String... distinctField);

    public <T extends Model> List<?> distinct(Class<T> modelClass, Map<String, Object> filter, String... distinctField);

    public <T extends Model> List<?> distinct(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions, String... distinctField);

    public <T extends MultiContextModel> T multiContextFindOne(Class<T> modelClass, Map<String, Object> filter);

    public <T extends MultiContextModel> List<T> multiContextFind(Class<T> modelClass, Map<String, Object> filter, String distinctFieldName);

    public <T extends MultiContextModel> List<T> multiContextFind(Class<T> modelClass, Map<String, Object> filter, String distinctFieldName, QueryOptions queryOptions);

    public <T extends MultiContextModel> T simpleContextFindOne(Class<T> modelClass, Map<String, Object> filter);

    public <T extends MultiContextModel> List<T> simpleContextFind(Class<T> modelClass, Map<String, Object> filter);

    public <T extends MultiContextModel> List<T> simpleContextFind(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions);

    public <T extends MultiContextModel> List<Id> simpleContextFindIdsOnly(Class<T> modelClass, Map<String, Object> filter);

    public <T extends MultiContextModel> List<Id> simpleContextFindIdsOnly(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions);

    public <T extends MultiContextModel> List<T> contextFind(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions);

    public <T extends Model> T findOne(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> Long count(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> Long count(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions);

    public <T extends Model> T add(T entity);

    public <T extends Model> List<T> addAll(List<T> entities);

    public <T extends Model> void update(T entity);

    public <T extends Model> void update(T entity, Map<String, Object> filter);

    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert);

    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert, boolean multi);

    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert, boolean multi, String... updateFields);

    public <T extends Model> void updateAll(List<T> entities);

    public <T extends Model> void remove(T entity);

    public <T extends Model> void remove(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> T findSnapshot(Class<T> modelClass, Id id, Integer version);

    public <T extends Model> T findSnapshot(Class<T> modelClass, Id id, Integer version, QueryOptions queryOptions);

    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id);

    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, QueryOptions queryOptions);

    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, Integer[] versions);

    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, Integer[] versions, QueryOptions queryOptions);

    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Map<String, Object> filter);

    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions);

    public void appendAttributeCondition(AttributeTargetObject targetObject, String attributeCode, Object value, Map<String, Object> query);

    public <T extends Model> void clearCaches(Class<T> modelClass);
}
