package com.geecommerce.core.system.rest.v1;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.rest.AbstractResource;
import com.google.inject.Inject;

@Path("/v1/caches")
public class CacheResource extends AbstractResource {
    private final CacheManager cacheManager;

    @Inject
    protected CacheResource(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @POST
    @Path("clear/{cacheName}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response clearCache(@PathParam("cacheName") String cacheName) {
        Cache<String, ?> cache = cacheManager.getCache(cacheName);

        if (cache != null)
            cache.emptyCache();

        return ok();
    }
}
