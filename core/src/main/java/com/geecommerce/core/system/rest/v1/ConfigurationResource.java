package com.geecommerce.core.system.rest.v1;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.jersey.inject.ModelParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.model.ConfigurationProperty;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Path("/v1/configurations")
public class ConfigurationResource extends AbstractResource {
    private final RestService service;

    @Inject
    public ConfigurationResource(RestService service) {
        this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getConfigurationProperties(@FilterParam Filter filter) {
        List<ConfigurationProperty> cp = service.get(ConfigurationProperty.class, filter.getParams(),
            queryOptions(filter));
        return ok(checked(cp));
        // return ok(checked(service.get(ConfigurationProperty.class,
        // filter.getParams(), queryOptions(filter))));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public ConfigurationProperty getConfigurationProperty(@PathParam("id") Id id) {
        ConfigurationProperty cp = service.get(ConfigurationProperty.class, id);
        return checked(cp);
        // return checked(service.get(ConfigurationProperty.class, id));
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createConfigurationProperty(@ModelParam ConfigurationProperty configurationProperty) {
        return created(service.create(configurationProperty));
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateConfigurationProperty(@PathParam("id") Id id, Update update) {
        if (id != null && update != null) {
            ConfigurationProperty ur = checked(service.get(ConfigurationProperty.class, id));
            ur.set(update.getFields());
            service.update(ur);

            return ok(ur);
        }

        return notFound();
    }
}
