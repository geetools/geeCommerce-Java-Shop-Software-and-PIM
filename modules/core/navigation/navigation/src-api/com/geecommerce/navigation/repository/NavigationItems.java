package com.geecommerce.navigation.repository;

import java.util.List;

import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.navigation.model.NavigationItem;

public interface NavigationItems extends Repository {
    public NavigationItem havingKey(String key);

    public List<NavigationItem> havingParent(NavigationItem parent);

    public NavigationItem withLevelAndPosition(Integer level, Integer position, Id rootId);

    public List<NavigationItem> havingRoot(Id rootId);

    public List<NavigationItem> withLevel(Integer level, Id rootId);

    public List<NavigationItem> withTargetObject(ObjectType targetObjectType, Id targetObjectId);

    public List<NavigationItem> withTargetObject(ObjectType targetObjectType, Id targetObjectId, Id rootId);
}
