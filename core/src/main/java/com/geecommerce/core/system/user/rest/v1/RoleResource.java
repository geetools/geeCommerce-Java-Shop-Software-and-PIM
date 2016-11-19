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
import com.geecommerce.core.system.user.model.Role;
import com.geecommerce.core.system.user.service.UserService;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Path("/v1/roles")
public class RoleResource extends AbstractResource {
    private final RestService service;
    private final UserService userService;

    @Inject
    public RoleResource(RestService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getRoles(@FilterParam Filter filter) {
        return ok(service.get(Role.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Role getRole(@PathParam("id") Id id) {
        return checked(service.get(Role.class, id));
    }

    @PUT
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateRole(@PathParam("id") String id, Update update) {
        if (id != null) {
            Role role = checked(service.get(Role.class, new Id(id)));
            role.set(update.getFields());
            service.update(role);
        }
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createRole(Update update) {
        Role role = app.getModel(Role.class);
        role.set(update.getFields());
        role = service.create(role);
        return created(role);
    }

    @DELETE
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeRole(@PathParam("id") Id id) {
        if (id != null) {
            Role role = checked(service.get(Role.class, id));
            if (role != null) {
                service.remove(role);
            }
        }
    }

    @GET
    @Path("get-permissions")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getPermissions(@FilterParam Filter filter) {
        return ok(checked(service.get(Permission.class, filter.getParams(), queryOptions(filter))));
    }
}
