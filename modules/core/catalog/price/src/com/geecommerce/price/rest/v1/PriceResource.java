package com.geecommerce.price.rest.v1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.price.model.PriceType;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.swagger.annotations.Api;

@Api
@Singleton
@Path("/v1/prices")
public class PriceResource extends AbstractResource {
    private final RestService service;

    @Inject
    public PriceResource(RestService service) {
        this.service = service;
    }

    @GET
    @Path("types")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getPriceTypes(@FilterParam Filter filter) {
        return ok(checked(service.get(PriceType.class, filter.getParams(), queryOptions(filter))));
    }
}
