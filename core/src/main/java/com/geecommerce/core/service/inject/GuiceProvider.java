package com.geecommerce.core.service.inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.geecommerce.core.inject.ModuleInjector;
import com.geecommerce.core.reflect.ApplicationContextClassLoader;

public class GuiceProvider<T> implements Provider<T> {
    private final Class<T> interfaceClass;
    private final Class<T> defaultClass;

    private Class<?> loadedClass = null;

    private static final Logger log = LogManager.getLogger(GuiceProvider.class);

    public GuiceProvider(Class<T> interfaceClass, Class<T> defaultClass) {
	if (log.isTraceEnabled()) {
	    log.trace("Registering GuiceProvider for [interfaceClass=" + interfaceClass + ", defaultClass=" + defaultClass + ", interfaceClassLoader=" + interfaceClass.getClassLoader() + ", defaultClassLoader=" + defaultClass.getClassLoader() + "].");
	}

	this.interfaceClass = interfaceClass;
	this.defaultClass = defaultClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
	try {
	    Injector injector = ModuleInjector.get();

	    if (loadedClass == null) {
		// Use the custom ApplicationContextClassLoader to find class
		ClassLoader classLoader = new ApplicationContextClassLoader(interfaceClass.getClassLoader());

		if (log.isTraceEnabled()) {
		    log.trace("Attempting to load class from GuiceProvider [interfaceClass=" + interfaceClass + ", defaultClass=" + defaultClass + ", interfaceClassLoader=" + interfaceClass.getClassLoader() + ", defaultClassLoader="
			    + defaultClass.getClassLoader() + "].");
		}

		// Load merchant or store-specific class if one exists
		loadedClass = classLoader.loadClass(defaultClass.getName());
	    }

	    if (log.isTraceEnabled()) {
		log.trace("Loaded class '" + loadedClass + "' with classloader '" + loadedClass.getClassLoader() + "'.");
	    }

	    T obj = (T) injector.getInstance(loadedClass);

	    if (log.isTraceEnabled()) {
		log.trace("Returning object created by Guice: " + obj + ", classloader=" + (obj != null ? obj.getClass().getClassLoader() : null));
	    }

	    return obj;
	} catch (Throwable t) {
	    log.error("*** An error occcured while attempting to fetch " + interfaceClass + " from provider.");
	    log.catching(t);

	    throw new RuntimeException(t);
	}
    }
}
