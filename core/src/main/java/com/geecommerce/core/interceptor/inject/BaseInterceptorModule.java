package com.geecommerce.core.interceptor.inject;

import com.google.inject.AbstractModule;

/**
 * This simple class enables all method interceptors to have objects injected by
 * Guice when they are created in the
 * {@link com.geecommerce.core.interceptor.AspectMethodInterceptor}.
 * 
 * @author Michael Delamere
 * @see com.geecommerce.core.interceptor.AspectMethodInterceptor
 */
public class BaseInterceptorModule extends AbstractModule {
    /**
     * Configuration method called by the Guice Injector.
     */
    @Override
    protected void configure() {
        bind(this.getClass());
    }
}
