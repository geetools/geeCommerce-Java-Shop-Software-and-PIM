package com.geecommerce.cart.rest.web.v1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.geecommerce.cart.helper.CartHelper;
import com.geecommerce.cart.model.Cart;
import com.geecommerce.core.rest.AbstractWebResource;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.swagger.annotations.Api;

@Api
@Singleton
@Path("/v1/web/carts")
public class CartWebResource extends AbstractWebResource {

    private final RestService service;
    private final CartHelper cartHelper;

    @Inject
    public CartWebResource(RestService service, CartHelper cartHelper) {
        this.service = service;
        this.cartHelper = cartHelper;
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Cart getCart(@PathParam("id") Id id) {
        return checked(cartHelper.getCart());
        // return checked(service.get(Cart.class, id));
    }

    @GET
    @Path("{id}/data")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Cart getCartData(@PathParam("id") Id id) {
        return checked(service.get(Cart.class, id));
    }
}
