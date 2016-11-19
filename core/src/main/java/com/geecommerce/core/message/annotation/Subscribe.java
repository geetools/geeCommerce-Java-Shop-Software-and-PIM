package com.geecommerce.core.message.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.geecommerce.core.event.Run;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Subscribe {
    String value() default "";

    String message() default "";

    String[] messages() default {};

    Run run() default Run.SYNCHRONOUSLY;
}
