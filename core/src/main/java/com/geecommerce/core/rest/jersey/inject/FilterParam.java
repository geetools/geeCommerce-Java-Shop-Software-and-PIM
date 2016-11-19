package com.geecommerce.core.rest.jersey.inject;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ PARAMETER, METHOD, FIELD })
@Retention(RUNTIME)
@Documented
public @interface FilterParam {
}
