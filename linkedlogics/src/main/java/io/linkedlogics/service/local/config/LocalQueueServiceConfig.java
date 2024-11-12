package io.linkedlogics.service.local.config;

import io.linkedlogics.service.config.Config;
import io.linkedlogics.service.config.Prefix;
import io.linkedlogics.service.config.ServiceConfig;

@Prefix("services.queue")
public interface LocalQueueServiceConfig extends ServiceConfig {
	
	@Config(key = "queue-size", description = "Size of the execution queue")
	public Integer getQueueSize(Integer defaultValue);
}
