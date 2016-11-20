package com.geecommerce.guiwidgets.rest.v1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.model.ContentLayout;
import com.google.inject.Inject;

@Path("/v1/content-layouts")
public class ContentLayoutResource extends AbstractResource {
    private final RestService service;

    @Inject
    public ContentLayoutResource(RestService service) {
        this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getContentLayouts(@FilterParam Filter filter) {
        return ok(service.get(ContentLayout.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public ContentLayout getContentLayout(@PathParam("id") Id id) {
        return checked(service.get(ContentLayout.class, id));
    }

}
