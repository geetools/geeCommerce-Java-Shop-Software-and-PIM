package com.geecommerce.core.system.rest.v1;

import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.jersey.inject.ModelParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.repository.RequestContexts;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/v1/request-contexts")
public class RequestContextResource extends AbstractResource
{
    private final RestService service;
    private final RequestContexts requestContexts;

    @Inject
    public RequestContextResource(RestService service, RequestContexts requestContexts)
    {
        this.service = service;
        this.requestContexts = requestContexts;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getRequestContexts(@FilterParam Filter filter)
    {
        return ok(service.get(RequestContext.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public RequestContext getRequestContext(@PathParam("id") Id id)
    {
        return checked(service.get(RequestContext.class, id));
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createRequestContext(@ModelParam RequestContext requestContext)
    {
        return created(service.create(requestContext));
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateRequestContext(@PathParam("id") Id id, Update update)
    {
        if (id != null && update != null)
        {
            RequestContext rq = checked(service.get(RequestContext.class, id));
            rq.set(update.getFields());
            service.update(rq);

            return ok(rq);
        }

        return notFound();
    }




}
