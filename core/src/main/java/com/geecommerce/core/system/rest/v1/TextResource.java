package com.geecommerce.core.system.rest.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.core.Char;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.jersey.inject.ModelParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.model.ContextMessage;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Path("/v1/text")
public class TextResource extends AbstractResource {
    private final RestService service;

    @Inject
    public TextResource(RestService service) {
        this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getContextMessages(@FilterParam Filter filter) {
        return ok(checked(service.get(ContextMessage.class, filter.getParams(), queryOptions(filter))));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public ContextMessage getContextMessage(@PathParam("id") Id id) {
        return checked(service.get(ContextMessage.class, id));
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createContextMessage(@ModelParam ContextMessage contextMessage) {
        return created(service.create(contextMessage));
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateContextMessage(@PathParam("id") Id id, Update update) {
        if (id != null && update != null) {
            ContextMessage ur = checked(service.get(ContextMessage.class, id));
            ur.set(update.getFields());
            service.update(ur);

            return ok(ur);
        }

        return notFound();
    }

    @PUT
    @Path("{id}/{locale}")
    @Consumes({ MediaType.TEXT_PLAIN })
    public Response updateContextMessage(@PathParam("id") Id id, @PathParam("locale") String localeStr,
        String message) {
        if (id != null && localeStr != null && message != null) {
            ContextMessage cm = checked(service.get(ContextMessage.class, id));
            ContextObject<String> ctxMessage = cm.getValue();

            String lang = localeStr;

            if (localeStr.indexOf(Char.UNDERSCORE) != -1) {
                lang = localeStr.substring(0, localeStr.indexOf(Char.UNDERSCORE));
            }

            ctxMessage.addOrUpdate(lang, message);

            service.update(cm);

            return ok();
        }

        return notFound();
    }
}
