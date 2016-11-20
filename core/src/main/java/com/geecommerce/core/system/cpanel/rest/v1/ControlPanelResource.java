package com.geecommerce.core.system.cpanel.rest.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.jersey.inject.ModelParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.cpanel.attribute.model.AttributeTab;
import com.geecommerce.core.system.cpanel.attribute.model.AttributeTabMapping;
import com.geecommerce.core.system.cpanel.model.ControlPanel;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.Ids;
import com.google.inject.Inject;

@Path("/v1/control-panels")
public class ControlPanelResource extends AbstractResource {
    private final RestService service;

    @Inject
    public ControlPanelResource(RestService service) {
        this.service = service;
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createControlPanel(@ModelParam ControlPanel controlPanel) {
        return created(service.create(controlPanel));
    }

    @POST
    @Path("{id}/attribute-tabs")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createAttributeTab(@PathParam("id") Id controlPanelId,
        @ModelParam AttributeTab cpanelAttributeTab) {
        checked(service.get(ControlPanel.class, controlPanelId));

        if (controlPanelId != null && cpanelAttributeTab != null) {
            if (cpanelAttributeTab.getControlPanelId() == null)
                cpanelAttributeTab.setControlPanelId(controlPanelId);

            if (controlPanelId.equals(cpanelAttributeTab.getControlPanelId())) {
                return created(service.create(cpanelAttributeTab));
            } else {
                throwBadRequest("The attribute-tab's controlPanelId does not match the id in the API-URI");
            }
        }

        throwInternalServerError();

        return null;
    }

    @PUT
    @Path("{id}/attribute-tabs/{tabId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateAttributeTab(@PathParam("id") Id controlPanelId, @PathParam("tabId") Id attrTabId,
        Update update) {
        System.out.println("UPDATING : " + controlPanelId + " - " + attrTabId + " - " + update.getFields());

        if (controlPanelId != null && attrTabId != null && update != null) {
            checked(service.get(ControlPanel.class, controlPanelId));

            AttributeTab attrTab = checked(service.get(AttributeTab.class, attrTabId));
            attrTab.set(update.getFields());

            service.update(attrTab);
        }
    }

    @GET
    @Path("{id}/attribute-tabs")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getAttributeTabs(@PathParam("id") Id controlPanelId, @FilterParam Filter filter) {
        checked(service.get(ControlPanel.class, controlPanelId));

        return ok(service.get(AttributeTab.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}/attribute-tabs/group/{targetObjectId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getAttributeTabsForGroup(@PathParam("id") Id controlPanelId,
        @PathParam("targetObjectId") Id targetObjectId, @FilterParam Filter filter) {
        checked(service.get(ControlPanel.class, controlPanelId));

        if (targetObjectId != null) {
            filter.getParams().put(Attribute.Col.TARGET_OBJECT_ID, targetObjectId);
        }

        return ok(service.get(AttributeTab.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}/attribute-tabs/{tabId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public AttributeTab getAttributeTab(@PathParam("id") Id controlPanelId, @PathParam("tabId") Id attrTabId) {
        checked(service.get(ControlPanel.class, controlPanelId));

        return checked(service.get(AttributeTab.class, attrTabId));
    }

    @POST
    @Path("{id}/attribute-tabs/{tabId}/mapping")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createAttributeTabsMapping(@PathParam("id") Id controlPanelId, @PathParam("tabId") Id attrTabId,
        @ModelParam List<AttributeTabMapping> attributeTabsMapping) {
        checked(service.get(ControlPanel.class, controlPanelId));

        List<AttributeTabMapping> createdList = new ArrayList<>();

        if (controlPanelId != null && attrTabId != null && attributeTabsMapping != null
            && attributeTabsMapping.size() > 0) {
            for (AttributeTabMapping attrTabMapping : attributeTabsMapping) {
                attrTabMapping.setTabId(attrTabId);

                createdList.add(service.create(attrTabMapping));
            }
        } else {
            throwInternalServerError();
        }

        return created(createdList);
    }

    @GET
    @Path("{id}/attribute-tabs/{tabId}/attributes")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getTabAttributes(@PathParam("id") Id controlPanelId, @PathParam("tabId") Id tabId) {
        checked(service.get(ControlPanel.class, controlPanelId));

        if (tabId != null) {
            // Check that the tab and attribute exist first.
            checked(service.get(AttributeTab.class, tabId));

            Filter f = new Filter();
            f.append(AttributeTabMapping.Col.TAB_ID, tabId);

            return ok(service.get(AttributeTabMapping.class, f.getParams(),
                QueryOptions.builder().sortBy(AttributeTabMapping.Col.POSITION).build()));
        } else {
            throwBadRequest(
                "TabId cannot be null in requestURI. Expecting: control-panels/{id}/attribute-tabs/{tabId}/attributes");
        }

        return null;
    }

    @GET
    @Path("{id}/attribute-tabs/attributes")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getAllTabAttributes(@PathParam("id") Id controlPanelId) {
        checked(service.get(ControlPanel.class, controlPanelId));

        if (controlPanelId != null) {
            Filter f = new Filter();
            f.append(AttributeTab.Col.CONTROL_PANEL_ID, controlPanelId);

            List<AttributeTab> attributeTabs = checked(service.get(AttributeTab.class, f.getParams(),
                QueryOptions.builder().sortBy(AttributeTab.Col.POSITION).build()));

            f = new Filter();
            f.append(AttributeTabMapping.Col.TAB_ID, Ids.toIdList(attributeTabs));

            return ok(service.get(AttributeTabMapping.class, f.getParams(),
                QueryOptions.builder().sortBy(AttributeTabMapping.Col.POSITION).build()));
        } else {
            throwBadRequest(
                "TabId cannot be null in requestURI. Expecting: control-panels/{id}/attribute-tabs/{tabId}/attributes");
        }

        return null;
    }

    @GET
    @Path("{id}/attribute-tabs/{tabId}/notTabAttributes")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getNotTabAttributes(@PathParam("id") Id controlPanelId, @PathParam("tabId") Id tabId,
        @FilterParam Filter filter) {
        List<Id> attributeIds = new LinkedList<>();

        if (tabId != null) {
            // Check that the tab and attribute exist first.
            checked(service.get(AttributeTab.class, tabId));

            Filter attributeTabFilter = new Filter();
            attributeTabFilter.append(AttributeTabMapping.Col.TAB_ID, tabId);

            List<AttributeTabMapping> attributeTabMappings = service.get(AttributeTabMapping.class,
                attributeTabFilter.getParams(),
                QueryOptions.builder().sortBy(AttributeTabMapping.Col.POSITION).build());

            for (AttributeTabMapping attributeTabMapping : attributeTabMappings) {
                attributeIds.add(attributeTabMapping.getAttributeId());
            }
        } else {
            throwBadRequest(
                "TabId cannot be null in requestURI. Expecting: control-panels/{id}/attribute-tabs/{tabId}/freeAttributes");
        }

        Map<String, Object> attributeTabFilter = new HashMap<>();
        Map<String, Object> attributeNotInFilter = new HashMap<>();
        attributeNotInFilter.put("$nin", attributeIds);
        attributeTabFilter.put(Attribute.Col.ID, attributeNotInFilter);
        attributeTabFilter.putAll(filter.getParams());

        if (tabId != null) {
            AttributeTab attributeTab = service.get(AttributeTab.class, tabId);
            attributeTabFilter.put(Attribute.Col.TARGET_OBJECT_ID, attributeTab.getTargetObjectId());
        }

        return ok(service.get(Attribute.class, attributeTabFilter, queryOptions(filter)));
    }

    @PUT
    @Path("{id}/attribute-tabs/{tabId}/attributes/{attributeId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response addAttributeToTab(@PathParam("id") Id controlPanelId, @PathParam("tabId") Id tabId,
        @PathParam("attributeId") Id attributeId) {
        checked(service.get(ControlPanel.class, controlPanelId));

        if (tabId != null && attributeId != null) {
            // Check that the tab and attribute exist first.
            checked(service.get(AttributeTab.class, tabId));
            checked(service.get(Attribute.class, attributeId));

            Filter f = new Filter();
            f.append(AttributeTabMapping.Col.TAB_ID, tabId);
            f.append(AttributeTabMapping.Col.ATTRIBUTE_ID, attributeId);

            List<AttributeTabMapping> existingMapping = service.get(AttributeTabMapping.class, f.getParams());

            // Only add the new mapping if it does not exist yet.
            if (existingMapping == null || existingMapping.size() == 0) {
                AttributeTabMapping attrTabMapping = app.model(AttributeTabMapping.class);
                attrTabMapping.setTabId(tabId).setAttributeId(attributeId).setPosition(0);

                return ok(service.create(attrTabMapping));
            }
        } else {
            throwBadRequest(
                "TabId and attributeId cannot be null in requestURI. Expecting: control-panels/{id}/attribute-tabs/{tabId}/attributes/{attributeId}");
        }
        return null;
    }

    @DELETE
    @Path("{id}/attribute-tabs/{tabId}/attributes/{attributeId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeAttributeFromTab(@PathParam("id") Id controlPanelId, @PathParam("tabId") Id tabId,
        @PathParam("attributeId") Id attributeId) {
        checked(service.get(ControlPanel.class, controlPanelId));

        if (tabId != null && attributeId != null) {
            // Check that the tab and attribute exist first.
            checked(service.get(AttributeTab.class, tabId));
            // checked(service.get(Attribute.class, attributeId)); // TODO: we
            // cannot delete removed attributes with this.

            Filter f = new Filter();
            f.append(AttributeTabMapping.Col.TAB_ID, tabId);
            f.append(AttributeTabMapping.Col.ATTRIBUTE_ID, attributeId);

            List<AttributeTabMapping> existingMapping = service.get(AttributeTabMapping.class, f.getParams());

            // Only add the new mapping if it does not exist yet.
            if (existingMapping != null && existingMapping.size() > 0) {
                for (AttributeTabMapping attrTabMapping : existingMapping) {
                    service.remove(attrTabMapping);
                }
            }
        } else {
            throwBadRequest(
                "TabId and attributeId cannot be null in requestURI. Expecting: control-panels/{id}/attribute-tabs/{tabId}/attributes/{attributeId}");
        }
    }

    @PUT
    @Path("{id}/options/positions")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateAttributeTabPositions(@PathParam("id") Id id, HashMap<String, Integer> positionsMap) {
        System.out.println(positionsMap);

        if (id != null && positionsMap != null && positionsMap.size() > 0) {
            AttributeTab attributeTab = checked(service.get(AttributeTab.class, id));

            Set<String> keys = positionsMap.keySet();

            for (String key : keys) {
                Id optionId = Id.valueOf(key);
                Integer pos = positionsMap.get(key);

                AttributeTabMapping attributeTabMapping = checked(service.get(AttributeTabMapping.class, optionId));

                if (attributeTabMapping.getTabId().equals(attributeTab.getId())) {
                    attributeTabMapping.setPosition(pos);
                    service.update(attributeTabMapping);
                }
            }
        }
    }

    @DELETE
    @Path("{id}")
    public void removeAttributeTab(@PathParam("id") Id id) {
        AttributeTab attributeTab = checked(service.get(AttributeTab.class, id));

        System.out.println("--- Removing attribute-tab: " + attributeTab);
        service.remove(attributeTab);
    }

}
