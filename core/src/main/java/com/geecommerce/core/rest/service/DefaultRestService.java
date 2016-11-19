package com.geecommerce.core.rest.service;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.rest.repository.RestRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Service
public class DefaultRestService implements RestService {
    private final RestRepository restRepository;

    @Inject
    public DefaultRestService(RestRepository restRepository) {
        this.restRepository = restRepository;
    }

    @Override
    public <T extends Model> T get(Class<T> modelClass, Id id) {
        return restRepository.findById(modelClass, id);
    }

    @Override
    public <T extends Model> T get(Class<T> modelClass, Id id, QueryOptions queryOptions) {
        return restRepository.findById(modelClass, id, queryOptions);
    }

    @Override
    public <T extends Model> List<T> get(Class<T> modelClass) {
        return restRepository.findAll(modelClass);
    }

    @Override
    public <T extends Model> List<T> get(Class<T> modelClass, Map<String, Object> filter) {
        return get(modelClass, filter, null);
    }

    @Override
    public <T extends Model> List<T> get(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions) {
        return restRepository.find(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends Model> List<Id> getIds(Class<T> modelClass) {
        return restRepository.findIds(modelClass);
    }

    @Override
    public <T extends Model> List<Id> getIds(Class<T> modelClass, Map<String, Object> filter) {
        return restRepository.findIds(modelClass, filter);
    }

    @Override
    public <T extends Model> List<Id> getIds(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions) {
        return restRepository.findIds(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends Model> T create(T model) {
        return restRepository.add(model);
    }

    @Override
    public <T extends Model> void update(T model) {
        restRepository.update(model);
    }

    @Override
    public <T extends Model> void remove(T model) {
        restRepository.remove(model);
    }

    @Override
    public <T extends Model> void clearCaches(Class<T> modelClass) {
        restRepository.clearCaches(modelClass);
    }

    @Override
    public <T extends Model> T getSnapshot(Class<T> modelClass, Id id, Integer version) {
        return restRepository.findSnapshot(modelClass, id, version);
    }

    @Override
    public <T extends Model> T getSnapshot(Class<T> modelClass, Id id, Integer version, QueryOptions queryOptions) {
        return restRepository.findSnapshot(modelClass, id, version, queryOptions);
    }

    @Override
    public <T extends Model> List<T> getSnapshots(Class<T> modelClass, Id id) {
        return restRepository.findSnapshots(modelClass, id);
    }

    @Override
    public <T extends Model> List<T> getSnapshots(Class<T> modelClass, Id id, QueryOptions queryOptions) {
        return restRepository.findSnapshots(modelClass, id, queryOptions);
    }

    @Override
    public <T extends Model> List<T> getSnapshots(Class<T> modelClass, Id id, Integer[] versions) {
        return restRepository.findSnapshots(modelClass, id, versions);
    }

    @Override
    public <T extends Model> List<T> getSnapshots(Class<T> modelClass, Id id, Integer[] versions, QueryOptions queryOptions) {
        return restRepository.findSnapshots(modelClass, id, versions, queryOptions);
    }

    @Override
    public <T extends Model> List<T> getSnapshots(Class<T> modelClass, Map<String, Object> filter) {
        return restRepository.findSnapshots(modelClass, filter);
    }

    @Override
    public <T extends Model> List<T> getSnapshots(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions) {
        return restRepository.findSnapshots(modelClass, filter, queryOptions);
    }
}
