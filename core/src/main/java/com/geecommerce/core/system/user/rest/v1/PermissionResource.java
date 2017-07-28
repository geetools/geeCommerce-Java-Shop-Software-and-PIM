package com.geecommerce.core.system.user.rest.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.user.model.Permission;
import com.geecommerce.core.system.user.service.UserService;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.swagger.annotations.Api;

@Api
@Singleton
@Path("/v1/permissions")
public class PermissionResource extends AbstractResource {
    private final UserService userService;
    private final RestService service;

    @Inject
    public PermissionResource(UserService userService, RestService service) {
        this.userService = userService;
        this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getPermissions(@FilterParam Filter filter) {
        return ok(service.get(Permission.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Permission getPermission(@PathParam("id") Id id) {
        return checked(service.get(Permission.class, id));
    }

    @PUT
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updatePermission(@PathParam("id") String id, Update update) {
        if (id != null) {
            Permission q = checked(service.get(Permission.class, new Id(id)));
            q.set(update.getFields());
            service.update(q);
        }
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createPermission(Update update) {
        Permission q = app.model(Permission.class);
        q.set(update.getFields());
        q = service.create(q);
        return created(q);
    }

    @DELETE
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removePermission(@PathParam("id") Id id) {
        if (id != null) {
            Permission q = checked(service.get(Permission.class, id));
            if (q != null) {
                service.remove(q);
            }
        }
    }
}
