package com.geecommerce.core.interceptor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as intercepting another method in the classpath. Note that
 * only methods that have been marked as
 * {@link com.geecommerce.core.interceptor.annotation.Interceptable} can be
 * intercepted.
 * 
 * @see com.geecommerce.core.interceptor.AspectMethodInterceptor
 * @see com.geecommerce.core.interceptor.AbstractMethodInterceptor
 * @see com.geecommerce.core.interceptor.annotation.Interceptable
 * 
 * @author Michael Delamere
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Intercept {
    Class<?> type() default Object.class;

    String name() default "";

    String method();

    int order() default 100;
}
