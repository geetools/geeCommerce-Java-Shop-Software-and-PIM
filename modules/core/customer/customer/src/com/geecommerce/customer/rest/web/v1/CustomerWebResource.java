package com.geecommerce.customer.rest.web.v1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.core.rest.AbstractWebResource;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.type.Id;
import com.geecommerce.customer.model.Customer;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.swagger.annotations.Api;

@Api
@Singleton
@Path("/v1/web/customers")
public class CustomerWebResource extends AbstractWebResource {
    private final RestService service;

    @Inject
    public CustomerWebResource(RestService service) {
        this.service = service;
    }

    @GET
    @Path("/isLoggedIn")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response isCustomerLoggedIn() {
        return ok(app.isCustomerLoggedIn());
    }

    @GET
    @Path("/getLoggedInCustomer")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Customer getLoggedInCustomer() {
        return app.getLoggedInCustomer();
    }

}
