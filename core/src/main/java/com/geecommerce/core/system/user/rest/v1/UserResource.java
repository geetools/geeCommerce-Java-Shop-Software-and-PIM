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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geecommerce.core.authentication.Passwords;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.security.DefaultCredentialsMatcher;
import com.geecommerce.core.system.user.model.Role;
import com.geecommerce.core.system.user.model.User;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Path("/v1/users")
public class UserResource extends AbstractResource {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserResource.class);
    private final RestService service;

    @Inject
    public UserResource(RestService service) {
        this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getUsers(@FilterParam Filter filter) {
        return ok(service.get(User.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public User getUser(@PathParam("id") Id id) {
        return checked(service.get(User.class, id));
    }

    @PUT
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateUser(@PathParam("id") String id, Update update) {
        if (id != null) {
            User user = checked(service.get(User.class, new Id(id)));
            user.set(update.getFields());
            service.update(user);
        }
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createUser(Update update) {
        User user = app.getModel(User.class);
        user.set(update.getFields());

        try {
            byte[] randomSalt = Passwords.getRandomSalt();
            user.setPassword(DefaultCredentialsMatcher.encryptPassword("TG7462y", randomSalt)).setSalt(randomSalt);
        } catch (Exception exc) {
            LOGGER.warn("Cannot generate password.", exc);
        }

        user = service.create(user);
        return created(user);
    }

    @DELETE
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeUser(@PathParam("id") Id id) {
        if (id != null) {
            User user = checked(service.get(User.class, id));
            if (user != null) {
                service.remove(user);
            }
        }
    }

    @GET
    @Path("get-roles")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getRoles(@FilterParam Filter filter, @PathParam("userId") Id userId) {
        return ok(checked(service.get(Role.class, filter.getParams(), queryOptions(filter))));
    }
}
