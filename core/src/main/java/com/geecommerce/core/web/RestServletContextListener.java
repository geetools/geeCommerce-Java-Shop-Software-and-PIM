package com.geecommerce.core.web;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.geecommerce.core.inject.ModuleInjector;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class RestServletContextListener extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
	return ModuleInjector.get().createChildInjector(new JerseyServletModule() {
	    @Override
	    protected void configureServlets() {
		// Route all requests through GuiceContainer
		serve("/restws/*").with(GuiceContainer.class, ImmutableMap.of(JSONConfiguration.FEATURE_POJO_MAPPING, "true"));
	    }
	});
    }
}
