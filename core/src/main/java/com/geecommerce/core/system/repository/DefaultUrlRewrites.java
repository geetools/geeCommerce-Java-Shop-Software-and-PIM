package com.geecommerce.core.system.repository;

import java.util.LinkedHashMap;
import java.util.Map;

import org.boon.Str;

import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.TargetSupport;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.core.service.persistence.mongodb.MongoQueries;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.IdSupport;
import com.google.inject.Inject;

public class DefaultUrlRewrites extends AbstractRepository implements UrlRewrites {
    final MongoDao mongoDao;

    @Inject
    public DefaultUrlRewrites(MongoDao mongoDao) {
        this.mongoDao = mongoDao;
    }

    @Override
    public Dao dao() {
        return this.mongoDao;
    }

    @Override
    public UrlRewrite havingURI(String uri) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(UrlRewrite.Col.ENABLED, true);
        MongoQueries.addCtxObjFilter(filter, UrlRewrite.Col.REQUEST_URI, uri);
        return multiContextFindOne(UrlRewrite.class, filter);
    }

    @Override
    public UrlRewrite forProductList(Id id) {
        return forTargetObject(id, ObjectType.PRODUCT_LIST);
    }

    @Override
    public UrlRewrite forProduct(Id id) {
        return forTargetObject(id, ObjectType.PRODUCT);
    }

    @Override
    public UrlRewrite forRetailStore(Id id) {
        return forTargetObject(id, ObjectType.RETAIL_STORE);
    }

    @Override
    public UrlRewrite forCMS(Id id) {
        return forTargetObject(id, ObjectType.CMS);
    }

    @Override
    public UrlRewrite forTargetObject(Id id, ObjectType objType) {
        if (id == null || objType == null)
            return null;

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(UrlRewrite.Col.TARGET_OBJECT_TYPE, objType.toId());
        filter.put(UrlRewrite.Col.TARGET_OBJECT_ID, id);
        filter.put(UrlRewrite.Col.ENABLED, true);

        return multiContextFindOne(UrlRewrite.class, filter);
    }

    @Override
    public UrlRewrite forTargetObject(IdSupport targetObject) {
        if (targetObject == null || targetObject.getId() == null)
            return null;

        if (!(targetObject instanceof TargetSupport))
            throw new IllegalArgumentException(
                "TargetObject must support the '" + TargetSupport.class.getName() + "' interface");

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(UrlRewrite.Col.TARGET_OBJECT_ID, targetObject.getId());
        filter.put(UrlRewrite.Col.ENABLED, true);

        return multiContextFindOne(UrlRewrite.class, filter);
    }

    @Override
    public UrlRewrite forTargetURI(String targetURI) {
        if (targetURI == null)
            return null;

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(UrlRewrite.Col.TARGET_URL, targetURI);
        filter.put(UrlRewrite.Col.ENABLED, true);

        return multiContextFindOne(UrlRewrite.class, filter);
    }

    @Override
    public boolean contains(String requestURI, ObjectType objType, Id ignoreTargetObjectId) {
        if (Str.isEmpty(requestURI))
            return false;

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put("req_uri.val", requestURI);
        filter.put(UrlRewrite.Col.TARGET_OBJECT_TYPE, objType.toId());

        Map<String, Object> notEqualsId = new LinkedHashMap<>();
        notEqualsId.put("$ne", ignoreTargetObjectId);

        filter.put(UrlRewrite.Col.TARGET_OBJECT_ID, notEqualsId);
        filter.put(UrlRewrite.Col.ENABLED, true);

        return count(UrlRewrite.class, filter, QueryOptions.builder().noCache(true).build()) > 0;
    }
}
