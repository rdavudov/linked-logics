package io.linkedlogics.service.local.config;

import io.linkedlogics.service.config.Config;
import io.linkedlogics.service.config.Prefix;
import io.linkedlogics.service.config.ServiceConfig;

@Prefix("services.evaluator")
public interface LocalEvaluatorServiceConfig extends ServiceConfig {
	
	@Config(key = "check-syntax")
	public Boolean getCheckSyntax(Boolean defaultValue);
}
