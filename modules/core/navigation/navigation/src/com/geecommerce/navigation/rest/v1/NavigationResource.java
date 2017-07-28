package com.geecommerce.navigation.rest.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.ConfigurationKey;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.util.Strings;
import com.geecommerce.navigation.helper.NavigationHelper;
import com.geecommerce.navigation.model.NavigationItem;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.swagger.annotations.Api;

@Api
@Singleton
@Path("/v1/navigation")
public class NavigationResource extends AbstractResource {
    private final RestService service;
    private final NavigationHelper navigationHelper;

    @Inject
    public NavigationResource(RestService service, NavigationHelper navigationHelper) {
        this.service = service;
        this.navigationHelper = navigationHelper;
    }

    @GET
    @Path("/roots")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getRootNavigationItems(@FilterParam Filter filter) {
        filter.append("level", 0);
        List<NavigationItem> navItems = service.get(NavigationItem.class, filter.getParams(), queryOptions(filter));
        return ok(navItems);
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getNavigationItems(@FilterParam Filter filter) {
        List<NavigationItem> navItems = service.get(NavigationItem.class, filter.getParams(), queryOptions(filter));

        try {
            Json.toJson(navItems);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return ok(navItems);
    }

    @POST
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public NavigationItem createNavigationItem(@PathParam("id") Id parentId) {
        NavigationItem parentNavItem = null;

        if (parentId != null) {
            parentNavItem = service.get(NavigationItem.class, parentId);
        }

        NavigationItem navItem = app.model(NavigationItem.class);
        navItem.setEnabled(false);
        navItem.setParent(parentNavItem);
        navItem.setRootId(parentNavItem.getRootId());
        navItem.setPosition(999);
        if (parentNavItem != null)
            navItem.setLevel(parentNavItem.getLevel() + 1);
        else
            navItem.setLevel(1);

        navItem = service.create(navItem);

        return checked(navItem);
    }

    @DELETE
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeNavigationItem(@PathParam("id") Id id) {
        NavigationItem navItem = service.get(NavigationItem.class, id);
        removeItem(navItem);
    }

    private void removeItem(NavigationItem navItem) {
        if (navItem != null) {
            service.remove(navItem);
        }

        if (navItem.getChildren() != null) {
            for (NavigationItem child : navItem.getChildren()) {
                removeItem(child);
            }
        }
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateNavigationItem(@PathParam("id") Id id, Update update) {
        NavigationItem item = service.get(NavigationItem.class, id);
        item.set(update.getFields());

        if (!update.getFields().containsKey("targetObjectId")) {
            item.setTargetObjectType(null);
            item.setTargetObjectId(null);
        }
        service.update(item);
        return ok(item);
    }

    @PUT
    @Path("tree")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateNavigationTree(HashMap<String, String> treeMap) {
        if (treeMap != null && treeMap.size() > 0) {
            List<NavigationItem> navItems = service.get(NavigationItem.class);

            for (NavigationItem item : navItems) {
                if (treeMap.containsKey(item.getId().toString())) {
                    Map<String, Object> node = Json.fromJson(treeMap.get(item.getId().toString()), HashMap.class);
                    item.setParentId(Id.parseId((String) node.get("parent")));
                    item.setLevel(((Long) node.get("level")).intValue());
                    item.setPosition(((Long) node.get("position")).intValue());
                    service.update(item);
                }
            }
        }
    }

    @PUT
    @Path("saveTree")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response saveTree(Object object) {
        HashMap<String, Object> treeMap = (HashMap<String, Object>) object;

        NavigationItem navigationItem = navigationHelper.populateNavigationTree(treeMap);

        if (navigationItem.getId() != null) {
            // navigationHelper.cloneNavigationTree(navigationItem.getId());
            navigationHelper.removeNavigationList(navigationItem.getId());
        }

        setKey(navigationItem);
        navigationItem = navigationHelper.createNavigationTree(navigationItem);

        return ok(navigationItem);
    }

    private void setKey(NavigationItem navigationItem) {
        String defaultLanguage = app.cpStr_(ConfigurationKey.I18N_CPANEL_DEFAULT_EDIT_LANGUAGE);

        if (navigationItem.getKey() == null || navigationItem.getKey().isEmpty()) {
            if (navigationItem.getLabel() != null) {
                String title = navigationItem.getLabel().getClosestValue(defaultLanguage);

                if (title == null || title.isEmpty()) {
                    title = navigationItem.getLabel().getClosestValue();
                }

                if (title != null && !title.isEmpty()) {
                    String key = Strings.slugify2(title).replace(Char.MINUS, Char.UNDERSCORE);

                    while (key.indexOf(Str.UNDERSCORE_2X) != -1)
                        key = key.replace(Str.UNDERSCORE_2X, Str.UNDERSCORE);

                    if (key.endsWith(Str.UNDERSCORE))
                        key = key.substring(0, key.length() - 1);

                    navigationItem.setKey(key);
                }
            }
        }
    }

}
