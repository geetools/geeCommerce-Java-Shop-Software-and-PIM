package com.geecommerce.core.rest.jersey;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.shiro.authc.AuthenticationException;

import com.google.inject.Singleton;

@Provider
@Singleton
public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable t) {
        if (t instanceof AuthenticationException) {
            return Response.status(Status.FORBIDDEN).entity(t.getMessage()).build();
        }
        if (t instanceof WebApplicationException) {
            return ((WebApplicationException) t).getResponse();
        } else {
            t.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(t.getMessage()).build();
        }
    }
}
