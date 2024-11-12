package io.linkedlogics.js;

import java.util.List;

import io.linkedlogics.service.LinkedLogicsService;
import io.linkedlogics.service.ServiceProvider;
import io.linkedlogics.js.service.JsEvaluatorService;

public class JsServices extends ServiceProvider {
	@Override
	public List<LinkedLogicsService> getEvaluatingServices() {
		return List.of(new JsEvaluatorService());
	}
}