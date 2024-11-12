package io.linkedlogics.spring.service;

import io.linkedlogics.service.ServiceConfigurer;

public class SpringServiceConfigurer  extends ServiceConfigurer {
	public SpringServiceConfigurer() {
		configure(new SpringEvaluatorService());
	}
}