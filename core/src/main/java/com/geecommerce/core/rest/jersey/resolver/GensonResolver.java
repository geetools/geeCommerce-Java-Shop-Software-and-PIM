package com.geecommerce.core.rest.jersey.resolver;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.geecommerce.core.util.Json;
import com.google.inject.Singleton;
import com.owlike.genson.Genson;

@Provider
@Singleton
public class GensonResolver implements ContextResolver<Genson> {
    @Override
    public Genson getContext(Class<?> type) {
        return Json.genson();
    }
}
