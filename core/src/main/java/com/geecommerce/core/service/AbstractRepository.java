package com.geecommerce.core.service;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.service.persistence.PersistenceProvider;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

public abstract class AbstractRepository implements Repository {

    @Inject
    protected PersistenceProvider persistenceProvider;

    public Dao dao() {
        return null;
    }

    protected Dao dao(Class<? extends Model> modelClass) {
        Dao dao = this.dao();
        return dao == null ? persistenceProvider.provideDao(modelClass) : dao;
    }

    protected RepositorySupport repositorySupport(Class<? extends Model> modelClass) {
        return persistenceProvider.provideRepositorySupport(modelClass, dao(modelClass));
    }

    @Override
    public <T extends Model> T findById(Class<T> modelClass, Id id) {
        return repositorySupport(modelClass).findById(modelClass, id);
    }

    @Override
    public <T extends Model> T findById(Class<T> modelClass, Id id, QueryOptions queryOptions) {
        return repositorySupport(modelClass).findById(modelClass, id, queryOptions);
    }

    @Override
    public <T extends Model> List<T> findByIds(Class<T> modelClass, Id[] ids) {
        return repositorySupport(modelClass).findByIds(modelClass, ids);
    }

    @Override
    public <T extends Model> List<T> findByIds(Class<T> modelClass, Id[] ids, QueryOptions queryOptions) {
        return repositorySupport(modelClass).findByIds(modelClass, ids, queryOptions);
    }

    @Override
    public <T extends Model> T findByUniqueKey(Class<T> modelClass, String key, Object value) {
        return repositorySupport(modelClass).findByUniqueKey(modelClass, key, value);
    }

    @Override
    public <T extends Model> List<T> findAll(Class<T> modelClass) {
        return repositorySupport(modelClass).findAll(modelClass);
    }

    @Override
    public <T extends Model> List<T> findAll(Class<T> modelClass, QueryOptions queryOptions) {
        return repositorySupport(modelClass).findAll(modelClass, queryOptions);
    }

    @Override
    public <T extends Model> List<T> find(Class<T> modelClass, String key, Object value) {
        return repositorySupport(modelClass).find(modelClass, key, value);
    }

    @Override
    public <T extends Model> List<T> find(Class<T> modelClass, String key, Object value, QueryOptions queryOptions) {
        return repositorySupport(modelClass).find(modelClass, key, value, queryOptions);
    }

    @Override
    public <T extends Model> List<T> find(Class<T> modelClass, Map<String, Object> filter) {
        return repositorySupport(modelClass).find(modelClass, filter);
    }

    @Override
    public <T extends Model> List<T> find(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions) {
        return repositorySupport(modelClass).find(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends Model> List<Id> findIds(Class<T> modelClass) {
        return repositorySupport(modelClass).findIds(modelClass);
    }

    @Override
    public <T extends Model> List<Id> findIds(Class<T> modelClass, Map<String, Object> filter) {
        return repositorySupport(modelClass).findIds(modelClass, filter);
    }

    @Override
    public <T extends Model> List<Id> findIds(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions) {
        return repositorySupport(modelClass).findIds(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass) {
        return repositorySupport(modelClass).findData(modelClass);
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass, Map<String, Object> filter) {
        return repositorySupport(modelClass).findData(modelClass, filter);
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions) {
        return repositorySupport(modelClass).findData(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findDataByIds(Class<T> modelClass, Id[] ids) {
        return repositorySupport(modelClass).findDataByIds(modelClass, ids);
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findDataByIds(Class<T> modelClass, Id[] ids,
        QueryOptions queryOptions) {
        return repositorySupport(modelClass).findDataByIds(modelClass, ids, queryOptions);
    }

    @Override
    public <T extends Model> List<?> distinct(Class<T> modelClass, String... distinctField) {
        return repositorySupport(modelClass).distinct(modelClass, distinctField);
    }

    @Override
    public <T extends Model> List<?> distinct(Class<T> modelClass, Map<String, Object> filter,
        String... distinctField) {
        return repositorySupport(modelClass).distinct(modelClass, filter, distinctField);
    }

    @Override
    public <T extends Model> List<?> distinct(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions, String... distinctField) {
        return repositorySupport(modelClass).distinct(modelClass, filter, queryOptions, distinctField);
    }

    @Override
    public <T extends MultiContextModel> T multiContextFindOne(Class<T> modelClass, Map<String, Object> filter) {
        return repositorySupport(modelClass).multiContextFindOne(modelClass, filter);
    }

    public <T extends MultiContextModel> List<T> multiContextFind(Class<T> modelClass, Map<String, Object> filter,
        String distinctFieldName) {
        return repositorySupport(modelClass).multiContextFind(modelClass, filter, distinctFieldName);
    }

    @Override
    public <T extends MultiContextModel> List<T> multiContextFind(Class<T> modelClass, Map<String, Object> filter,
        String distinctFieldName, QueryOptions queryOptions) {
        return repositorySupport(modelClass).multiContextFind(modelClass, filter, distinctFieldName, queryOptions);
    }

    @Override
    public <T extends MultiContextModel> T simpleContextFindOne(Class<T> modelClass, Map<String, Object> filter) {
        return repositorySupport(modelClass).simpleContextFindOne(modelClass, filter);
    }

    @Override
    public <T extends MultiContextModel> List<T> simpleContextFind(Class<T> modelClass, Map<String, Object> filter) {
        return repositorySupport(modelClass).simpleContextFind(modelClass, filter);
    }

    @Override
    public <T extends MultiContextModel> List<T> simpleContextFind(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions) {
        return repositorySupport(modelClass).simpleContextFind(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends MultiContextModel> List<Id> simpleContextFindIdsOnly(Class<T> modelClass,
        Map<String, Object> filter) {
        return repositorySupport(modelClass).simpleContextFindIdsOnly(modelClass, filter);
    }

    @Override
    public <T extends MultiContextModel> List<Id> simpleContextFindIdsOnly(Class<T> modelClass,
        Map<String, Object> filter, QueryOptions queryOptions) {
        return repositorySupport(modelClass).findIds(modelClass, filter, queryOptions);
    }

    public <T extends MultiContextModel> List<T> contextFind(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions) {
        return repositorySupport(modelClass).contextFind(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends Model> T findOne(Class<T> modelClass, Map<String, Object> filter) {
        return repositorySupport(modelClass).findOne(modelClass, filter);
    }

    @Override
    public <T extends Model> Long count(Class<T> modelClass, Map<String, Object> filter) {
        return repositorySupport(modelClass).count(modelClass, filter);
    }

    @Override
    public <T extends Model> Long count(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions) {
        return repositorySupport(modelClass).count(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends Model> T add(T entity) {
        return repositorySupport(entity.getClass()).add(entity);
    }

    @Override
    public <T extends Model> List<T> addAll(List<T> entities) {
        return repositorySupport(entities.get(0).getClass()).addAll(entities);
    }

    @Override
    public <T extends Model> void update(T entity) {
        repositorySupport(entity.getClass()).update(entity);
    }

    @Override
    public <T extends Model> void update(T entity, Map<String, Object> filter) {
        repositorySupport(entity.getClass()).update(entity, filter);
    }

    @Override
    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert) {
        repositorySupport(entity.getClass()).update(entity, filter, upsert);
    }

    @Override
    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert, boolean multi) {
        repositorySupport(entity.getClass()).update(entity, filter, upsert, multi);
    }

    @Override
    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert, boolean multi,
        String... updateFields) {
        repositorySupport(entity.getClass()).update(entity, filter, upsert, multi, updateFields);
    }

    @Override
    public <T extends Model> void updateAll(List<T> entities) {
        repositorySupport(entities.get(0).getClass()).updateAll(entities);
    }

    @Override
    public <T extends Model> void remove(T entity) {
        repositorySupport(entity.getClass()).remove(entity);
    }

    @Override
    public <T extends Model> void remove(Class<T> modelClass, Map<String, Object> filter) {
        repositorySupport(modelClass).remove(modelClass, filter);
    }

    @Override
    public <T extends Model> T findSnapshot(Class<T> modelClass, Id id, Integer version) {
        return repositorySupport(modelClass).findSnapshot(modelClass, id, version);
    }

    @Override
    public <T extends Model> T findSnapshot(Class<T> modelClass, Id id, Integer version, QueryOptions queryOptions) {
        return repositorySupport(modelClass).findSnapshot(modelClass, id, version, queryOptions);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id) {
        return repositorySupport(modelClass).findSnapshots(modelClass, id);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, QueryOptions queryOptions) {
        return repositorySupport(modelClass).findSnapshots(modelClass, id, queryOptions);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, Integer[] versions) {
        return repositorySupport(modelClass).findSnapshots(modelClass, id, versions);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, Integer[] versions,
        QueryOptions queryOptions) {
        return repositorySupport(modelClass).findSnapshots(modelClass, id, versions, queryOptions);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Map<String, Object> filter) {
        return repositorySupport(modelClass).findSnapshots(modelClass, filter);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions) {
        return repositorySupport(modelClass).findSnapshots(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends Model> void appendAttributeCondition(Class<T> modelClass, AttributeTargetObject targetObject,
        String attributeCode, Object value, Map<String, Object> query) {
        repositorySupport(modelClass).appendAttributeCondition(targetObject, attributeCode, value, query);
    }

    @Override
    public <T extends Model> void clearCaches(Class<T> modelClass) {
        repositorySupport(modelClass).clearCaches(modelClass);
    }
}
