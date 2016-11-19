package com.geecommerce.core.elasticsearch.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface Indexable {
    String collection() default "";
}
