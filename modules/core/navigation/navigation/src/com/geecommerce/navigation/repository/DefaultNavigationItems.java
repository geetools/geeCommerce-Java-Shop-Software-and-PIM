package com.geecommerce.navigation.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.navigation.model.NavigationItem;

@Repository
public class DefaultNavigationItems extends AbstractRepository implements NavigationItems {
    @Override
    public NavigationItem havingKey(String key) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(NavigationItem.Col.KEY, key);
        filter.put(NavigationItem.Col.ENABLED, true);

        return multiContextFindOne(NavigationItem.class, filter);
    }

    @Override
    public NavigationItem withLevelAndPosition(Integer level, Integer position, Id rootId) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(NavigationItem.Col.LEVEL, level);
        filter.put(NavigationItem.Col.POSITION, position);
        filter.put(NavigationItem.Col.ROOT_ID, rootId);
        filter.put(NavigationItem.Col.ENABLED, true);

        return multiContextFindOne(NavigationItem.class, filter);
    }

    @Override
    public List<NavigationItem> withLevel(Integer level, Id rootId) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(NavigationItem.Col.LEVEL, level);
        filter.put(NavigationItem.Col.ROOT_ID, rootId);
        filter.put(NavigationItem.Col.ENABLED, true);

        return multiContextFind(NavigationItem.class, filter, NavigationItem.Col.KEY, QueryOptions.builder().sortBy(NavigationItem.Col.POSITION).build());
    }

    @Override
    public List<NavigationItem> havingRoot(Id rootId) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(NavigationItem.Col.ROOT_ID, rootId);

        return multiContextFind(NavigationItem.class, filter, NavigationItem.Col.KEY, QueryOptions.builder().sortBy(NavigationItem.Col.POSITION).build());
    }

    @Override
    public List<NavigationItem> havingParent(NavigationItem parent) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(NavigationItem.Col.PARENT_ID, parent.getId());
        filter.put(NavigationItem.Col.ENABLED, true);

        List<NavigationItem> childNavItems = multiContextFind(NavigationItem.class, filter, NavigationItem.Col.KEY,
            QueryOptions.builder().sortBy(NavigationItem.Col.LEVEL, NavigationItem.Col.POSITION).build());

        for (NavigationItem childNavItem : childNavItems) {
            childNavItem.setParent(parent);
        }

        return childNavItems;
    }

    @Override
    public List<NavigationItem> withTargetObject(ObjectType targetObjectType, Id targetObjectId) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(NavigationItem.Col.TARGET_OBJECT_TYPE, targetObjectType.toId());
        filter.put(NavigationItem.Col.TARGET_OBJECT_ID, targetObjectId);
        filter.put(NavigationItem.Col.ENABLED, true);

        return multiContextFind(NavigationItem.class, filter, NavigationItem.Col.KEY);
    }

    @Override
    public List<NavigationItem> withTargetObject(ObjectType targetObjectType, Id targetObjectId, Id rootId) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(NavigationItem.Col.TARGET_OBJECT_TYPE, targetObjectType.toId());
        filter.put(NavigationItem.Col.TARGET_OBJECT_ID, targetObjectId);
        filter.put(NavigationItem.Col.ROOT_ID, rootId);
        filter.put(NavigationItem.Col.ENABLED, true);

        return multiContextFind(NavigationItem.class, filter, NavigationItem.Col.KEY);
    }
}
