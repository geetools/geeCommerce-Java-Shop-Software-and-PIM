package com.geecommerce.core.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.geecommerce.core.cron.MisfireInstruction;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface Task {
    String name();

    String group();

    String schedule();

    int priority() default 99;

    MisfireInstruction onMisfire() default MisfireInstruction.DO_NOTHING;

    boolean recoverable() default false;

    boolean enabled() default true;

    boolean transaction() default false;
}
