package com.geecommerce.core.system.attribute.rest.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.ModelParam;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.type.Id;

@Path("/v1/attribute-options")
public class AttributeOptionResource extends AbstractResource {
    private final RestService service;
    private final AttributeService attributeService;

    @Inject
    public AttributeOptionResource(RestService service, AttributeService attributeService) {
	this.service = service;
	this.attributeService = attributeService;
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public AttributeOption getAttributeOption(@PathParam("id") Id id) {
	return checked(attributeService.getAttributeOption(id));
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createAttributeOption(@ModelParam AttributeOption attributeOption) {
	Response created = created(service.create(attributeOption));
	// Attribute attribute = service.get(Attribute.class, attributeOption.getAttributeId());
	// attribute.refreshOptions();
	service.clearCaches(Attribute.class);
	service.clearCaches(AttributeOption.class);
	return created;
    }

    @PUT
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateAttributeOption(@PathParam("id") Id id, Update update) {
	AttributeOption ao = checked(attributeService.getAttributeOption(id));
	ao.set(update.getFields());

	attributeService.updateAttributeOption(ao);
    }
}
