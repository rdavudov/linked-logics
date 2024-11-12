package io.linkedlogics.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Logic {
    String id();
    String returnAs() default "" ;
    int version() default 0;
    String description() default "";
    boolean returnAsync() default false;
    boolean returnMap() default false;
}
