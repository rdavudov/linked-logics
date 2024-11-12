package io.linkedlogics.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import io.linkedlogics.spring.bean.SpringServiceConfigurer;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SpringServiceConfigurer.class)
public @interface EnableLinkedLogics {
	SpringProfile[] value() default {};
}
