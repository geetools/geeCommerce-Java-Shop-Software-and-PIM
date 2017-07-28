package com.geecommerce.core.system.attribute.rest.v1;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
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

import com.geecommerce.core.enums.AttributeGroupMappingType;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeGroup;
import com.geecommerce.core.system.attribute.model.AttributeGroupMapping;
import com.geecommerce.core.type.Id;
import com.google.inject.Singleton;

import io.swagger.annotations.Api;

@Api
@Singleton
@Path("/v1/attribute-groups")
public class AttributeGroupResource extends AbstractResource {
    private final RestService service;

    @Inject
    public AttributeGroupResource(RestService service) {
        this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getAttributeGroups(@FilterParam Filter filter) {
        List<AttributeGroup> attributeGroups = service.get(AttributeGroup.class, filter.getParams(),
            queryOptions(filter));
        return ok(attributeGroups);
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public AttributeGroup getAttributeGroup(@PathParam("id") Id id) {
        return checked(service.get(AttributeGroup.class, id));
    }

    @GET
    @Path("{id}/fix")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public AttributeGroup getFixAttributeGroup(@PathParam("id") Id id) {
        List<AttributeGroup> attributeGroups = service.get(AttributeGroup.class);
        for (AttributeGroup attrGroup : attributeGroups) {
            service.update(attrGroup);
        }
        return null;
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateAttributeGroup(@PathParam("id") Id id, Update update) {
        AttributeGroup attributeGroup = checked(service.get(AttributeGroup.class, id));
        attributeGroup.set(update.getFields());
        service.update(attributeGroup);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createAttributeGroup(Update update) {
        AttributeGroup attributeGroup = app.model(AttributeGroup.class);
        attributeGroup.set(update.getFields());
        attributeGroup = service.create(attributeGroup);
        return created(attributeGroup);
    }

    @DELETE
    @Path("{id}")
    public void removeAttribute(@PathParam("id") Id id) {
        AttributeGroup attributeGroup = checked(service.get(AttributeGroup.class, id));

        System.out.println("--- Removing attribute-group: " + attributeGroup);
        service.remove(attributeGroup);
    }

    @GET
    @Path("{id}/notGroupAttributes")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getNotGroupAttributes(@PathParam("id") Id id, @FilterParam Filter filter) {
        List<Id> attributeIds = new LinkedList<>();

        if (id != null) {
            // Check that the tab and attribute exist first.
            AttributeGroup attributeGroup = service.get(AttributeGroup.class, id);

            if (attributeGroup != null && attributeGroup.getItems() != null) {
                attributeIds = attributeGroup.getItems().stream().map(AttributeGroupMapping::getId)
                    .collect(Collectors.toList());
                /* attributeIds.addAll(attributeGroup.getAttributeIds()); */
            }
        } else {
            throwBadRequest("GroupId cannot be null in requestURI. Expecting: {id}/notGroupAttributes");
        }

        Map<String, Object> attributeGroupFilter = new HashMap<>();
        Map<String, Object> attributeNotInFilter = new HashMap<>();
        attributeNotInFilter.put("$nin", attributeIds);
        attributeGroupFilter.put(Attribute.Col.ID, attributeNotInFilter);
        attributeGroupFilter.putAll(filter.getParams());

        return ok(service.get(Attribute.class, attributeGroupFilter, queryOptions(filter)));
    }

    @GET
    @Path("{id}/notGroupAttributeGroups")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getNotGroupAttributeGroups(@PathParam("id") Id id, @FilterParam Filter filter) {
        List<Id> attributeIds = new LinkedList<>();

        if (id != null) {
            // Check that the tab and attribute exist first.
            AttributeGroup attributeGroup = service.get(AttributeGroup.class, id);

            if (attributeGroup != null && attributeGroup.getItems() != null) {

                attributeIds = attributeGroup.getItems().stream().map(AttributeGroupMapping::getId)
                    .collect(Collectors.toList());
                attributeIds.add(id);
                /*
                 * attributeIds.addAll(attributeGroup.getAttributeIds());
                 * attributeIds.add(id);
                 */
            }
        } else {
            throwBadRequest("GroupId cannot be null in requestURI. Expecting: {id}/notGroupAttributes");
        }

        Map<String, Object> attributeGroupFilter = new HashMap<>();
        Map<String, Object> attributeNotInFilter = new HashMap<>();
        attributeNotInFilter.put("$nin", attributeIds);
        attributeGroupFilter.put(AttributeGroup.Col.ID, attributeNotInFilter);
        attributeGroupFilter.putAll(filter.getParams());

        return ok(service.get(AttributeGroup.class, attributeGroupFilter, queryOptions(filter)));
    }

    @PUT
    @Path("{id}/attributes/{attributeId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response addAttributeToGroup(@PathParam("id") Id id, @PathParam("attributeId") Id attributeId) {
        if (id != null && attributeId != null) {
            // Check that the tab and attribute exist first.
            AttributeGroup attributeGroup = service.get(AttributeGroup.class, id);
            checked(service.get(Attribute.class, attributeId));

            attributeGroup.addItem(attributeId, AttributeGroupMappingType.ATTRIBUTE);
            service.update(attributeGroup);
            return ok();

        } else {
            throwBadRequest(
                "TabId and attributeId cannot be null in requestURI. Expecting: control-panels/{id}/attribute-tabs/{tabId}/attributes/{attributeId}");
        }
        return null;
    }

    @PUT
    @Path("{id}/attribute-groups/{groupId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response addGroupToGroup(@PathParam("id") Id id, @PathParam("groupId") Id groupId) {
        if (id != null && groupId != null) {
            // Check that the tab and attribute exist first.
            AttributeGroup attributeGroup = service.get(AttributeGroup.class, id);
            checked(service.get(AttributeGroup.class, groupId));

            attributeGroup.addItem(groupId, AttributeGroupMappingType.ATTRIBUTE_GROUP);
            service.update(attributeGroup);
            return ok();

        } else {
            throwBadRequest(
                "TabId and attributeId cannot be null in requestURI. Expecting: control-panels/{id}/attribute-tabs/{tabId}/attributes/{attributeId}");
        }
        return null;
    }

    @DELETE
    @Path("{id}/items/{itemId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeAttributeFromGroup(@PathParam("id") Id id, @PathParam("itemId") Id itemId) {
        if (id != null) {
            // Check that the tab and attribute exist first.
            AttributeGroup attributeGroup = service.get(AttributeGroup.class, id);
            attributeGroup.removeItem(itemId);
            service.update(attributeGroup);

        } else {
            throwBadRequest(
                "TabId and attributeId cannot be null in requestURI. Expecting: control-panels/{id}/attribute-tabs/{tabId}/attributes/{attributeId}");
        }
    }

    @PUT
    @Path("{id}/positions")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateAttributeTabPositions(@PathParam("id") Id id, HashMap<String, Integer> positionsMap) {
        System.out.println(positionsMap);

        if (id != null && positionsMap != null && positionsMap.size() > 0) {
            AttributeGroup attributeGroup = checked(service.get(AttributeGroup.class, id));
            Set<String> keys = positionsMap.keySet();
            for (String key : keys) {
                Id optionId = Id.valueOf(key);
                Integer pos = positionsMap.get(key);

                Optional<AttributeGroupMapping> mapping = attributeGroup.getItems().stream()
                    .filter(x -> x.getId().equals(optionId)).findFirst();
                if (mapping.isPresent()) {
                    mapping.get().setPosition(pos);
                }
            }
            service.update(attributeGroup);
        }
    }
}
