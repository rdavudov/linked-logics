package io.linkedlogics.service.config;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Config {
	 String key();
	 String description() default "";
	 String type() default "";
	 boolean required() default false;
}
