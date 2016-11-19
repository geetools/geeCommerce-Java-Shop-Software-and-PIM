package com.geecommerce.core.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface Model {
    String value() default "";

    String collection() default "";

    String context() default "";

    boolean readCount() default false;

    boolean optimisticLocking() default false;

    boolean fieldAccess() default false;

    boolean autoPopulate() default true;

    boolean preload() default false;

    boolean history() default false;
}
