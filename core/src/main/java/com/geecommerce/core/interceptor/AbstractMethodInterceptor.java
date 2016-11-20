package com.geecommerce.core.interceptor;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Abstract class used for extending implementations of method interceptors. The
 * {@link com.geecommerce.core.interceptor.AspectMethodInterceptor} takes care
 * of aspect handling and finding matching method interceptors that extend this
 * class. The extending method interceptor must also annotate the class
 * definition with the annotation
 * {@link com.geecommerce.core.interceptor.annotation.Intercept}, otherwise it
 * will not be found. Note that it is not sufficient to <i>only</i> annotate the
 * method interceptor. The class to be intercepted must also allow this with the
 * {@link com.geecommerce.core.interceptor.annotation.Interceptable} annotation.
 * 
 * @see com.geecommerce.core.interceptor.AspectMethodInterceptor
 * @see com.geecommerce.core.interceptor.annotation.Interceptable
 * @see com.geecommerce.core.interceptor.annotation.Intercept
 * 
 * @author Michael Delamere
 */
public abstract class AbstractMethodInterceptor {
    protected MethodInvocation invocation = null;

    public void onBefore(Object[] args) {
        // override and implement in child class ...
    }

    public void onAfter(Object[] args) {
        // override and implement in child class ...
    }

    public void onAfterReturning(Object[] result) {
        // override and implement in child class ...
    }

    public void onAfterThrowing(Throwable e) {
        // override and implement in child class ...
    }
}
