package io.linkedlogics.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import io.linkedlogics.service.ServiceProvider;
import io.linkedlogics.spring.bean.SpringServiceConfigurer;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SpringServiceConfigurer.class)
public @interface SpringProfile {
	String[] profile() default {};
	Class<? extends ServiceProvider> storing() default ServiceProvider.class;
	Class<? extends ServiceProvider> messaging() default ServiceProvider.class;
	Class<? extends ServiceProvider> processing() default ServiceProvider.class;
	Class<? extends ServiceProvider> scheduling() default ServiceProvider.class;
	Class<? extends ServiceProvider> evaluating() default ServiceProvider.class;
	Class<? extends ServiceProvider> monitoring() default ServiceProvider.class;
	Class<? extends ServiceProvider> tracking() default ServiceProvider.class;
}
