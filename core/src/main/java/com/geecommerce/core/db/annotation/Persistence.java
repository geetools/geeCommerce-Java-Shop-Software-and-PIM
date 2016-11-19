package com.geecommerce.core.db.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.geecommerce.core.service.api.Model;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface Persistence {
    String value() default "";

    String name() default "";

    Class<? extends Model> model() default Model.class;

    String module() default "";
}
