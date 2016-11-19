package com.geecommerce.navigation.service;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.navigation.model.NavigationItem;
import com.geecommerce.navigation.repository.NavigationItems;
import com.google.inject.Inject;

@Service
public class DefaultNavigationService implements NavigationService {
    @Inject
    protected App app;

    protected final NavigationItems navigationItems;

    protected static final String DEFAULT_ROOT_NAV_ID_KEY = "navigation/default/root_id";
    protected static final String COOKIE_ROOT_KEY = "nav_root_id";

    @Inject
    public DefaultNavigationService(NavigationItems navigationItems) {
        this.navigationItems = navigationItems;
    }

    @Override
    public NavigationItem createNavigationItem(NavigationItem navigationItem) {
        return navigationItems.add(navigationItem);
    }

    @Override
    public void update(NavigationItem navigationItem) {
        navigationItems.update(navigationItem);
    }

    @Override
    public NavigationItem getNavigationItem(Id id) {
        if (id == null) {
            return null;
        }

        return navigationItems.findById(NavigationItem.class, id);
    }

    @Override
    public NavigationItem getNavigationItemByKey(String key) {
        if (key == null) {
            return null;
        }

        return navigationItems.havingKey(key);
    }

    @Override
    public NavigationItem getNavigationItem(Integer level, Integer position) {
        Id rootId = Id.valueOf(app.cpLong_(new StringBuilder("navigation/").append("default").append("/root_id").toString()));
        return getNavigationItem(level, position, rootId);
    }

    @Override
    public NavigationItem getNavigationItem(Integer level, Integer position, Id rootId) {
        if (level == null || position == null) {
            return null;
        }

        return navigationItems.withLevelAndPosition(level, position, rootId);
    }

    @Override
    public List<NavigationItem> getNavigationItems(Id... ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }

        return navigationItems.findByIds(NavigationItem.class, ids);
    }

    @Override
    public List<NavigationItem> getNavigationItems(Integer level, Id rootId) {
        if (level == null) {
            return null;
        }

        return navigationItems.withLevel(level, rootId);
    }

    @Override
    public List<NavigationItem> getNavigationItemsByTargetObject(ObjectType targetObjectType, Id targetObjectId) {
        if (targetObjectType == null || targetObjectId == null) {
            return null;
        }

        return navigationItems.withTargetObject(targetObjectType, targetObjectId);
    }

    @Override
    public NavigationItem getNavigationItemByTargetObject(ObjectType targetObjectType, Id targetObjectId, Id rootId) {
        if (targetObjectType == null || targetObjectId == null) {
            return null;
        }

        List<NavigationItem> navItems = navigationItems.withTargetObject(targetObjectType, targetObjectId, rootId);

        return navItems == null || navItems.isEmpty() ? null : navItems.get(0);
    }

    @Override
    public List<NavigationItem> getNavigationItemsByTargetObject(ObjectType targetObjectType, Id targetObjectId, Id rootId) {
        if (targetObjectType == null || targetObjectId == null) {
            return null;
        }

        return navigationItems.withTargetObject(targetObjectType, targetObjectId, rootId);
    }

    @Override
    public NavigationItem findRootNavigationItem() {
        NavigationItem rootNode = null;

        // Only check cookie if this method is being called within a
        // web-request, otherwise jobs will fail.
        if (app.isWebRequest()) {
            String cookieRootId = app.cookieGet(COOKIE_ROOT_KEY);
            if (cookieRootId != null && !cookieRootId.isEmpty()) {
                Id rootId = Id.valueOf(cookieRootId);
                if (rootId != null)
                    rootNode = navigationItems.findById(NavigationItem.class, rootId);
            }
        }

        // If root_id is still null, attempt to get a default value from the
        // configuration table.
        if (rootNode == null) {
            Long rootId = app.cpLong_(DEFAULT_ROOT_NAV_ID_KEY);

            if (rootId != null)
                rootNode = navigationItems.findById(NavigationItem.class, Id.valueOf(rootId));
        }

        // If there is no configuration at all anywhere, then we'll just attempt
        // to locate
        // the first navigation-item with level=0 and position=0.
        if (rootNode == null)
            rootNode = getNavigationItem(0, 0);

        return rootNode;
    }

    @Override
    public NavigationItem saveNavigationFromMap(Map<String, Object> navigationTree) {
        NavigationItem navigationItem = app.getModel(NavigationItem.class);
        // navigationItem.fromMap();

        return null;
    }
}
