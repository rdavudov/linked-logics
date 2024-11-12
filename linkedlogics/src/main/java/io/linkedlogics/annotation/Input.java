package io.linkedlogics.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Input {
    String value() default "";
    boolean required() default false;
    boolean returned() default false;
    String description() default "";
}
