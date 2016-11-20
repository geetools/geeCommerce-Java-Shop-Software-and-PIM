package com.geecommerce.navigation.widget;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.navigation.NavigationConstant;
import com.geecommerce.navigation.model.NavigationItem;
import com.geecommerce.navigation.service.NavigationService;
import com.google.inject.Inject;

@Widget(name = "navigation")
public class Navigation extends AbstractWidgetController implements WidgetController {
    // Navigation tree
    private NavigationItem rootNavItem = null;

    private final NavigationService navigationService;

    @Inject
    public Navigation(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @SuppressWarnings("unused")
    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        // Fetch options
        Id rootId = widgetCtx.getParam("root_id", Id.class);
        String name = widgetCtx.getParam("name");
        String type = widgetCtx.getParam("type");
        Integer fetchLevelFrom = widgetCtx.getParam("fetch_level_from", Integer.class);
        Integer fetchPosition = widgetCtx.getParam("fetch_position", Integer.class);

        // Display options
        Integer displayLevelOnly = widgetCtx.getParam("display_level_only", Integer.class);
        Integer displayLevelFrom = widgetCtx.getParam("display_level_from", Integer.class);
        String displayType = widgetCtx.getParam("display_type"); // FLAT or TREE
        String cssPrefix = widgetCtx.getParam("css_prefix");
        String customTemplate = widgetCtx.getParam("custom_template");

        if (rootId == null)
            rootId = locateRootId(type, name, fetchLevelFrom, fetchPosition);

        NavigationItem navItem = navigationService.getNavigationItem(rootId);

        if (navItem != null && fetchLevelFrom != null && fetchLevelFrom < navItem.getLevel()) {
            rootNavItem = navItem.traverseUpTo(fetchLevelFrom);
        } else {
            rootNavItem = navItem;
        }

        // Add top-navigation items to widget-context
        widgetCtx.setParam("rootNavItem", rootNavItem == null ? navItem : rootNavItem);

        //
        widgetCtx.setParam("rootNavItems", getRoots(locateRootIds(type)));

        if (customTemplate != null) {
            widgetCtx.render("navigation/" + customTemplate);
        } else {
            widgetCtx.render("navigation/navigation");
        }
    }

    public boolean isActive(NavigationItem navItem) {
        List<Id> navItemIds = app.registryGet(NavigationConstant.NAV_ITEM_IDS);

        if (navItemIds != null && navItemIds.size() > 0 && navItemIds.contains(navItem.getId()))
            return true;

        else
            return false;
    }

    protected List<Id> locateRootIds(String type) {
        List<Long> rootIds = app
            .cpLongList_(new StringBuilder("navigation/").append(type).append("/root_ids").toString());
        List<Id> ids = new ArrayList<>();
        if (rootIds != null) {
            for (Long id : rootIds) {
                ids.add(Id.valueOf(id));
            }
            return ids;
        }
        return null;
    }

    protected List<NavigationItem> getRoots(List<Id> ids) {
        List<NavigationItem> navigationItems = new ArrayList<>();

        if (ids == null)
            return null;

        for (Id id : ids) {
            navigationItems.add(navigationService.getNavigationItem(id));
        }
        return navigationItems;
    }

    protected Id locateRootId(String type, String key, Integer level, Integer position) {
        Long rootId = null;

        // If no specific root_id has been passed as a parameter, try getting it
        // from configuration table.
        if (type != null) {
            rootId = app.cpLong_(new StringBuilder("navigation/").append(type).append("/root_id").toString());
        }

        // If we still have no root_id and a key has been given, try that.
        if (rootId == null && key != null) {
            NavigationItem navItem = navigationService.getNavigationItemByKey(key);

            if (navItem != null)
                rootId = navItem.getId().longValue();
        }

        // Attempt to find the navigation-item with level and position.
        if (rootId == null && level != null) {
            NavigationItem navItem = navigationService.getNavigationItem(level, position == null ? 0 : position);
            if (navItem != null) {
                rootId = navItem.getId().longValue();
            }
        }

        // If root_id is still null, attempt to get a default value from the
        // configuration table.
        if (rootId == null) {
            NavigationItem navItem = navigationService.findRootNavigationItem();

            if (navItem != null)
                rootId = navItem.getId().longValue();
        }

        return rootId == null ? null : Id.valueOf(rootId);
    }

}
