package com.geecommerce.core.interceptor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as being interceptable. Classes wishing to intercept a method annotated with this annotation must be marked with the
 * {@link com.geecommerce.core.interceptor.annotation.Intercept} annotation.
 * 
 * @see com.geecommerce.core.interceptor.AspectMethodInterceptor
 * @see com.geecommerce.core.interceptor.AbstractMethodInterceptor
 * @see com.geecommerce.core.interceptor.annotation.Intercept
 * 
 * @author Michael Delamere
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Interceptable {
}
