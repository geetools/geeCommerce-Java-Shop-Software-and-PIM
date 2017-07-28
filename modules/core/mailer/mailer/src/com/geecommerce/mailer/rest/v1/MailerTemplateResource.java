package com.geecommerce.mailer.rest.v1;

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
import com.geecommerce.mailer.model.MailerTemplate;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.swagger.annotations.Api;

@Api
@Singleton
@Path("/v1/mailer-templates")
public class MailerTemplateResource extends AbstractResource {

    private final RestService service;

    @Inject
    public MailerTemplateResource(RestService service) {
        this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getMailerTemplates(@FilterParam Filter filter) {
        return ok(service.get(MailerTemplate.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public MailerTemplate getMailerTemplate(@PathParam("id") Id id) {
        return checked(service.get(MailerTemplate.class, id));
    }

}
