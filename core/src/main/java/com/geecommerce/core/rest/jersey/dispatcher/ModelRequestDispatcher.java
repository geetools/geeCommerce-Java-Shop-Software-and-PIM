package com.geecommerce.core.rest.jersey.dispatcher;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

public class ModelRequestDispatcher implements RequestDispatcher {
    private final RequestDispatcher wrappedDispatcher;

    ModelRequestDispatcher(RequestDispatcher wrappedDispatcher) {
	this.wrappedDispatcher = wrappedDispatcher;
    }

    @Override
    public void dispatch(Object resource, HttpContext context) {
	wrappedDispatcher.dispatch(resource, context);
    }
}
