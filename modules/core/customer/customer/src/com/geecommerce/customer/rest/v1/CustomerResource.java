package com.geecommerce.customer.rest.v1;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.type.Id;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.model.CustomerGroup;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import au.com.bytecode.opencsv.CSVWriter;
import io.swagger.annotations.Api;

@Api
@Singleton
@Path("/v1/customers")
public class CustomerResource extends AbstractResource {
    private final RestService service;

    @Inject
    public CustomerResource(RestService service) {
        this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getCustomers(@FilterParam Filter filter) {
        return ok(service.get(Customer.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Customer getCustomer(@PathParam("id") Id id) {
        return checked(service.get(Customer.class, id));
    }

    @PUT
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateCustomer(@PathParam("id") Id id, Update update) {
        if (id != null) {
            Customer c = checked(service.get(Customer.class, id));
            c.set(update.getFields());

            service.update(c);
        }
    }

    @GET
    @Path("groups")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getCustomerGroups(@FilterParam Filter filter) {
        return ok(checked(service.get(CustomerGroup.class, filter.getParams(), queryOptions(filter))));
    }

    @GET
    @Path("export/emails")
    // @Produces("text/plain")
    public Response getCustomerExportEmails(@FilterParam Filter filter, @Context HttpServletResponse response)
        throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"customer-emails.csv\"");
        PrintWriter printWriter = response.getWriter();
        CSVWriter writer = new CSVWriter(printWriter, ';');
        writer.writeNext(new String[] { "E-mail" });

        List<Customer> customers = service.get(Customer.class);
        for (Customer customer : customers) {
            if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
                writer.writeNext(new String[] { customer.getEmail() });
            }
        }
        writer.flush();
        writer.close();

        return Response.ok().build();
    }
}
