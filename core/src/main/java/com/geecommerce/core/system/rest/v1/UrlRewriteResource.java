package com.geecommerce.core.system.rest.v1;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.core.Str;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.jersey.inject.ModelParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Path("/v1/url-rewrites")
public class UrlRewriteResource extends AbstractResource {
    private final RestService service;

    private static final String REGEX_CACHE_NAME = "^cb\\/query.+";
    private static final String REGEX_QUERY_TARGET_OBJECT_ID = "^query.+tar_obj=%s.+";
    private static final String REGEX_QUERY_REQUEST_URI = "^query.+%s.+";

    @Inject
    public UrlRewriteResource(RestService service) {
        this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getURLRewrites(@FilterParam Filter filter) {
        return ok(service.get(UrlRewrite.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public UrlRewrite getURLRewrite(@PathParam("id") Id id) {
        return checked(service.get(UrlRewrite.class, id));
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createURLRewrite(@ModelParam UrlRewrite urlRewrite) {
        return created(service.create(urlRewrite));
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateURLRewrite(@PathParam("id") Id id, Update update) {
        if (id != null && update != null) {
            UrlRewrite ur = checked(service.get(UrlRewrite.class, id));
            ur.set(update.getFields());
            service.update(ur);

            // In most cases the DAO removes queries from the cache
            // automatically. Here it may not always work,
            // so we give the cache a little help in finding the right queries.
            invalidateCache(ur);

            return ok(ur);
        }

        return notFound();
    }

    private void invalidateCache(UrlRewrite urlRewrite) {
        if (urlRewrite.getTargetObjectId() != null)
            app.inject(CacheManager.class).invalidateWhereKeyLike(REGEX_CACHE_NAME,
                String.format(REGEX_QUERY_TARGET_OBJECT_ID, urlRewrite.getTargetObjectId().str()));

        ContextObject<String> requestURIs = urlRewrite.getRequestURI();

        if (requestURIs != null) {
            for (Map<String, Object> map : requestURIs) {
                String requestURI = (String) map.get(ContextObject.VALUE);
                app.inject(CacheManager.class).invalidateWhereKeyLike(REGEX_CACHE_NAME,
                    String.format(REGEX_QUERY_REQUEST_URI, requestURI.replace(Str.SLASH, Str.SLASH_ESCAPED)));
            }
        }
    }
}
