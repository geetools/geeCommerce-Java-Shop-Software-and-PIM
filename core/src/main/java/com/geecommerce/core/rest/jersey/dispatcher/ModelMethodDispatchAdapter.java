package com.geecommerce.core.rest.jersey.dispatcher;

import javax.ws.rs.ext.Provider;

import com.google.inject.Singleton;
import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;

@Singleton
@Provider
public class ModelMethodDispatchAdapter implements ResourceMethodDispatchAdapter {
    @Override
    public ResourceMethodDispatchProvider adapt(ResourceMethodDispatchProvider provider) {
        return new ModelMethodDispatchProvider(provider);
    }
}
