package com.geecommerce.core.rest.jersey.dispatcher;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

public class ModelMethodDispatchProvider implements ResourceMethodDispatchProvider {
    private final ResourceMethodDispatchProvider wrappedProvider;

    ModelMethodDispatchProvider(ResourceMethodDispatchProvider wrappedProvider) {
        this.wrappedProvider = wrappedProvider;
    }

    @Override
    public RequestDispatcher create(AbstractResourceMethod arm) {
        return new ModelRequestDispatcher(wrappedProvider.create(new ResourceMethodWrapper(arm)));
    }
}
