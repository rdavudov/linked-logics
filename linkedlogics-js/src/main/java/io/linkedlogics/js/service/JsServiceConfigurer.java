package io.linkedlogics.js.service;

import io.linkedlogics.service.ServiceConfigurer;

public class JsServiceConfigurer extends ServiceConfigurer {
	public JsServiceConfigurer() {
		configure(new JsEvaluatorService());
	}
}

