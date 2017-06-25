package com.geecommerce.core.service.annotation;

        import java.lang.annotation.Documented;
        import java.lang.annotation.ElementType;
        import java.lang.annotation.Inherited;
        import java.lang.annotation.Retention;
        import java.lang.annotation.RetentionPolicy;
        import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface Column {
    String value() default "";

    String name() default "";

    boolean autoPopulate() default true;
}
