package io.linkedlogics.service.local.config;

import java.util.Optional;

import io.linkedlogics.service.config.Config;
import io.linkedlogics.service.config.Prefix;
import io.linkedlogics.service.config.ServiceConfig;

@Prefix("services.scheduler")
public interface LocalSchedulerServiceConfig extends ServiceConfig {

	@Config(key = "threads", description = "Number of threads to execute scheduled tasks")
	public Optional<Integer> getThreads();
}
