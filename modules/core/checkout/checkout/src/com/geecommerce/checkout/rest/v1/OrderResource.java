package com.geecommerce.checkout.rest.v1;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.service.CheckoutService;
import com.geecommerce.core.Str;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.swagger.annotations.Api;

@Api
@Singleton
@Path("/v1/orders")
public class OrderResource extends AbstractResource {
    private final CheckoutService checkoutService;
    private final RestService service;

    @Inject
    public OrderResource(CheckoutService checkoutService, RestService service) {
        this.checkoutService = checkoutService;
        this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getOrders(@FilterParam Filter filter) {
        if (!Str.isEmpty(filter.getString("transactionId"))) {
            List<Order> orders = new ArrayList<>();

            Order order = checkoutService.getOrderByTransaction(filter.getString("transactionId"));

            if (order != null)
                orders.add(order);

            return ok(orders);
        } else {
            return ok(checkoutService.getOrders(filter.getParams(), queryOptions(filter)));
        }
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Order getOrder(@PathParam("id") Id id) {
        return checked(checkoutService.getOrder(id));
    }
}
