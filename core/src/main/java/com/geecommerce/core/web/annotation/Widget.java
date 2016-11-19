package com.geecommerce.core.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Widget {
    String value() default "";

    String name() default "";

    boolean cms() default false;

    boolean js() default false;

    boolean css() default false;
}
