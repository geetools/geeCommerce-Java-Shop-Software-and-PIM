package com.geecommerce.core.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.geecommerce.core.App;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.service.QueryMetadata;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.user.model.User;
import com.geecommerce.core.system.user.pojo.ClientSession;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

public abstract class AbstractResource {
    @Inject
    protected App app;

    protected static final String PLAIN_TEXT = "text/plain";

    protected static final String KEY_AUTHENTICATED_CLIENT = "authenticated.client";

    @Context
    protected HttpServletRequest servletRequest;

    @Context
    protected HttpServletResponse servletResponse;

    @Context
    protected HttpHeaders httpHeaders;

    @Context
    protected UriInfo uriInfo;

    protected final HttpServletRequest request() {
        return servletRequest;
    }

    protected final HttpServletResponse response() {
        return servletResponse;
    }

    protected final HttpHeaders httpHeaders() {
        return httpHeaders;
    }

    protected final UriInfo uriInfo() {
        return uriInfo;
    }

    protected final boolean storeHeaderExists() {
        return app.storeHeaderExists();
    }

    protected final Id getStoreFromHeader() {
        return app.getStoreFromHeader();
    }

    protected void appendHeaders() {
        HttpServletRequest request = app.servletRequest();
        HttpServletResponse response = app.servletResponse();

        HttpSession session = request.getSession(false);

        if (session != null) {
            long currentTime = System.currentTimeMillis();
            long sessionTimesOutAt = session.getLastAccessedTime() + (session.getMaxInactiveInterval() * 1000);
            long sessionTimesOutIn = sessionTimesOutAt - currentTime;

            response.setHeader("GC-Timeout-At", String.valueOf(sessionTimesOutAt));
            response.setHeader("GC-Timeout-In", String.valueOf(sessionTimesOutIn));
        }
    }

    protected Response created(Object entity) {
        appendHeaders();

        if (entity == null) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } else {
            return Response.status(Status.CREATED).entity(entity).build();
        }
    }

    protected Response deleted() {
        return Response.status(Status.NO_CONTENT).build();
    }

    protected <T> T checked(T object) {
        appendHeaders();

        if (object == null)
            throwNotFound();

        if (object instanceof List<?>) {
            if (((List<?>) object).size() == 0)
                throwNotFound();
        } else if (object.getClass().isArray()) {
            if (Arrays.asList(object).size() == 0)
                throwNotFound();
        } else if (object instanceof Map<?, ?>) {
            if (((Map<?, ?>) object).size() == 0)
                throwNotFound();
        }

        return object;
    }

    protected Response notFound() {
        appendHeaders();

        return notFound(null);
    }

    protected Response notFound(String message) {
        appendHeaders();

        return response(Status.NOT_FOUND, message);
    }

    protected Response badRequest(String message) {
        appendHeaders();

        return response(Status.BAD_REQUEST, message);
    }

    protected Response internalServerError(String message) {
        appendHeaders();

        return response(Status.INTERNAL_SERVER_ERROR, message);
    }

    protected void throwNotFound() {
        appendHeaders();

        throwNotFound(null);
    }

    protected void throwNotFound(String message) {
        appendHeaders();

        throw new WebApplicationException(notFound(message));
    }

    protected void throwBadRequest() {
        appendHeaders();

        throwBadRequest(null);
    }

    protected void throwBadRequest(String message) {
        appendHeaders();

        throw new WebApplicationException(badRequest(message));
    }

    protected void throwInternalServerError() {
        appendHeaders();

        throwInternalServerError(null);
    }

    protected void throwInternalServerError(String message) {
        appendHeaders();

        throw new WebApplicationException(internalServerError(message));
    }

    protected Response response(Status status, String message) {
        appendHeaders();

        if (message != null)
            return Response.status(status).type(PLAIN_TEXT).entity(message).build();

        else
            return Response.status(status).build();
    }

    protected <T extends Model> Response ok() {
        appendHeaders();

        return Response.status(Status.OK).build();
    }

    protected <T extends Model> Response ok(T model) {
        appendHeaders();

        return Response.status(Status.OK).entity(ResponseWrapper.builder().set(model).build()).build();
    }

    protected <T extends Model> Response ok(List<T> models) {
        appendHeaders();

        return Response.status(Status.OK).entity(appendMetadata(ResponseWrapper.builder().set(models)).build()).build();
    }

    protected <T extends Model> Response ok(List<T> models, Map<String, Object> metaData) {
        appendHeaders();

        ResponseWrapper.Builder builder = appendMetadata(ResponseWrapper.builder().set(models));

        for (Map.Entry<String, Object> entry : metaData.entrySet()) {
            builder.appendMetadata(entry.getKey(), entry.getValue());
        }

        return Response.status(Status.OK).entity(builder.build()).build();
    }

    protected ResponseWrapper.Builder appendMetadata(ResponseWrapper.Builder builder) {
        QueryMetadata qmd = app.getLastQueryMetadata();

        if (qmd != null && qmd.getCount() != null) {
            builder.appendMetadata("totalCount", qmd.getCount());
        }

        return builder;
    }

    protected Response ok(Object result) {
        appendHeaders();

        return ok("results", result);
    }

    protected Response ok(String fieldName, Object result) {
        appendHeaders();

        return Response.status(Status.OK).entity(ResponseWrapper.builder().set(fieldName, result).build()).build();
    }

    protected QueryOptions queryOptions(Filter filter) {
        if (filter != null)
            return queryOptions(filter.getFields(), filter.getAttributes(), filter.getSort(), filter.getOffset(),
                filter.getLimit(), filter.isNoCache());
        else
            return queryOptions(null, null, null, null, null, null);
    }

    protected QueryOptions queryOptions(List<String> fields, List<String> attributes, List<String> sortBy, Long offset,
        Integer limit, Boolean noCache) {
        // List<Id> allowedScopes = getAllowedScopes();
        //
        // List<Id> allowedMerchants =
        // Contexts.getAllowedMerchants(allowedScopes);
        // List<Id> allowedStores = Contexts.getAllowedStores(allowedScopes);
        // List<Id> allowedRequestContexts =
        // Contexts.getAllowedRequestContexts(allowedScopes);

        // System.out.println("ALLOWED-MERCHANTS ::: " + allowedMerchants);
        // System.out.println("ALLOWED-STORES ::: " + allowedStores);
        // System.out.println("ALLOWED-REQUEST-CONTEXTS ::: " +
        // allowedRequestContexts);

        if (fields == null && attributes == null && sortBy == null && offset == null && limit == null && !noCache)
            return null;

        else
            return QueryOptions.builder().fetchFields(fields).fetchAttributes(attributes).sortBy(sortBy)
                .fromOffset(offset).limitTo(limit).noCache(noCache).provideCount(true).build();

        // .setLimitToMerchants(allowedMerchants)
        // .setLimitToStores(allowedStores)
        // .setLimitToRequestContexts(allowedRequestContexts);
    }

    protected List<Id> getAllowedScopes() {
        if (clientSessionExists()) {
            return getClientSession().getAllowedScopes();
        }

        return null;
    }

    protected void initClientSession(User user) {
        Subject subject = SecurityUtils.getSubject();

        if (subject.isAuthenticated()) {

            ClientSession clientSession = new ClientSession(user.getUsername(),
                user.getForename() + " " + user.getSurname(), user.getScopeIds());

            System.out.println("CREATING SESSION ***** ClientSession: " + clientSession);

            Session sess = subject.getSession(true);

            System.out.println("CREATING SESSION ***** Session: " + sess.getId());

            sess.setAttribute(KEY_AUTHENTICATED_CLIENT, clientSession);
        }
    }

    protected ClientSession getClientSession() {
        if (!sessionExists())
            return null;

        return (ClientSession) getSession(false).getAttribute(KEY_AUTHENTICATED_CLIENT);
    }

    protected boolean clientSessionExists() {
        if (!sessionExists())
            return false;

        return getSession(false).getAttribute(KEY_AUTHENTICATED_CLIENT) != null;
    }

    protected boolean sessionExists() {
        return getSession(false) != null;
    }

    private Session getSession(boolean create) {
        Subject subject = SecurityUtils.getSubject();

        if (subject != null) {
            return subject.getSession(create);
        }

        return null;
    }

    protected List<Id> getStoreContext() {
        return null;
        // /servletRequest.getHeaders()
    }
}
