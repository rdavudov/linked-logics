package io.linkedlogics.spring;

import java.util.List;

import io.linkedlogics.service.LinkedLogicsService;
import io.linkedlogics.service.ServiceProvider;
import io.linkedlogics.spring.service.SpringEvaluatorService;

@EnableLinkedLogics(@SpringProfile(profile = "production"))
public class SpringServices extends ServiceProvider {

	@Override
	public List<LinkedLogicsService> getEvaluatingServices() {
		return List.of(new SpringEvaluatorService());
	}
}
