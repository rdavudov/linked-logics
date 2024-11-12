package io.linkedlogics.jdbc.service.config;

import io.linkedlogics.service.config.Config;
import io.linkedlogics.service.config.Prefix;
import io.linkedlogics.service.config.ServiceConfig;

@Prefix("services.limit")
public interface JdbcLimitServiceConfig extends ServiceConfig {
	
	@Config(key = "cleanup.interval", description = "Cleanup interval of old limits", required = true)
	public Integer getCleanupInterval();
}
