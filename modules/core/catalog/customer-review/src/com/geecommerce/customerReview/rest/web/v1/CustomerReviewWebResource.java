package com.geecommerce.customerReview.rest.web.v1;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.rest.AbstractWebResource;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.type.Id;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customerReview.model.CustomerReview;
import com.geecommerce.customerReview.service.CustomerReviewService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.swagger.annotations.Api;

@Api
@Singleton
@Path("/v1/web/customer-reviews")
public class CustomerReviewWebResource extends AbstractWebResource {

    private final RestService restService;
    private final CustomerReviewService customerReviewService;

    private static final Logger LOG = LogManager.getLogger(CustomerReviewWebResource.class);

    @Inject
    public CustomerReviewWebResource(RestService restService, CustomerReviewService customerReviewService) {
        this.restService = restService;
        this.customerReviewService = customerReviewService;
    }

    @POST
    @Path("{id}/helpful")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response markAsHelpful(@PathParam("id") Id id) {

        HashMap<String, String> result = new HashMap<>();

        if (!app.isCustomerLoggedIn()) {
            result.put("error", "not logged in");
            return ok(result);
        }

        CustomerReview customerReview = null;
        Customer loggedInCustomer = app.getLoggedInCustomer();
        if (loggedInCustomer != null) {
            Id customerId = loggedInCustomer.getId();
            customerReview = customerReviewService.getCustomerReview(id);
            if (customerReview != null) {
                if (!customerReview.getRatedByCustomer(customerId)) {
                    List<Id> thinkHelpful = customerReview.getThinkHelpful();
                    if (!thinkHelpful.contains(customerId.longValue()))
                        thinkHelpful.add(customerId);
                    customerReviewService.updateReview(customerReview);
                }
            }
        }

        result.put("ok", "marked as helpful");
        return ok(result);
    }

    @POST
    @Path("{id}/unhelpful")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response markAsUnHelpful(@PathParam("id") Id id) {

        HashMap<String, String> result = new HashMap<>();

        if (!app.isCustomerLoggedIn()) {
            result.put("error", "not logged in");
            return ok(result);
        }

        Customer loggedInCustomer = app.getLoggedInCustomer();
        if (loggedInCustomer != null) {
            Id customerId = loggedInCustomer.getId();
            CustomerReview customerReview = customerReviewService.getCustomerReview(id);
            if (customerReview != null) {
                if (!customerReview.getRatedByCustomer(customerId)) {
                    List<Id> thinkUnhelpful = customerReview.getThinkUnhelpful();
                    if (!thinkUnhelpful.contains(customerId.longValue()))
                        thinkUnhelpful.add(customerId);
                    customerReviewService.updateReview(customerReview);
                }
            }
        }

        result.put("ok", "marked as unhelpful");
        return ok(result);
    }

}
