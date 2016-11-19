package com.geecommerce.core.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.geecommerce.core.event.Event;
import com.geecommerce.core.event.Run;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Observe {
    String name() default "";

    Event[] event();

    String[] merchants() default {};

    String[] stores() default {};

    String[] views() default {};

    Run run() default Run.SYNCHRONOUSLY;
}
