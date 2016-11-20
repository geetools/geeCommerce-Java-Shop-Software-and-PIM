package com.geecommerce.navigation.service;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.navigation.model.NavigationItem;

public interface NavigationService extends Service {
    public NavigationItem createNavigationItem(NavigationItem navItem);

    public void update(NavigationItem navItem);

    public NavigationItem getNavigationItem(Id id);

    public NavigationItem getNavigationItemByKey(String key);

    public NavigationItem getNavigationItem(Integer level, Integer position);

    public NavigationItem getNavigationItem(Integer level, Integer position, Id rootId);

    public List<NavigationItem> getNavigationItems(Id... ids);

    public List<NavigationItem> getNavigationItems(Integer level, Id rootId);

    public List<NavigationItem> getNavigationItemsByTargetObject(ObjectType targetObjectType, Id targetObjectId);

    public NavigationItem getNavigationItemByTargetObject(ObjectType targetObjectType, Id targetObjectId, Id rootId);

    public List<NavigationItem> getNavigationItemsByTargetObject(ObjectType targetObjectType, Id targetObjectId,
        Id rootId);

    public NavigationItem findRootNavigationItem();

    public NavigationItem saveNavigationFromMap(Map<String, Object> navigationTree);
}
