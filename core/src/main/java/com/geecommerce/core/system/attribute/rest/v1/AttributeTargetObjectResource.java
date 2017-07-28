package com.geecommerce.core.system.attribute.rest.v1;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.google.inject.Singleton;

import io.swagger.annotations.Api;

@Api
@Singleton
@Path("/v1/attribute-target-objects")
public class AttributeTargetObjectResource extends AbstractResource {
    private final RestService service;

    @Inject
    public AttributeTargetObjectResource(RestService service) {
        this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getAttributeTargetObjects(@FilterParam Filter filter) {
        List<AttributeTargetObject> attributeTargetObjects = service.get(AttributeTargetObject.class,
            filter.getParams(), queryOptions(filter));

        return ok(attributeTargetObjects);
    }
}
