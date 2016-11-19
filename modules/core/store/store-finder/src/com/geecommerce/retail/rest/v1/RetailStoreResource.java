package com.geecommerce.retail.rest.v1;

import com.google.inject.Inject;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.retail.service.RetailStoreService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Andrey on 02.11.2015.
 */
@Path("/v1/retail-stores")
public class RetailStoreResource extends AbstractResource {
    private final RetailStoreService service;

    @Inject
    public RetailStoreResource(RetailStoreService service) {
	this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getRetailStores(@FilterParam Filter filter) {
	return ok(service.getRetailStores());
    }
}
