package com.geecommerce.core.system.repository;

import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.IdSupport;

public interface UrlRewrites extends Repository {
    public UrlRewrite havingURI(String uri);

    public UrlRewrite forProductList(Id id);

    public UrlRewrite forProduct(Id id);

    public UrlRewrite forRetailStore(Id id);

    public UrlRewrite forCMS(Id id);

    public UrlRewrite forTargetObject(Id id, ObjectType objType);

    public UrlRewrite forTargetObject(IdSupport targetObject);

    public UrlRewrite forTargetURI(String targetURI);

    public boolean contains(String requestURI, ObjectType objType, Id ignoreTargetObjectId);
}
