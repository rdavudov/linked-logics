package io.linkedlogics.redis.service.config;

import java.util.Optional;

import io.linkedlogics.service.config.Config;
import io.linkedlogics.service.config.Prefix;
import io.linkedlogics.service.config.ServiceConfig;

@Prefix("services.process")
public interface RedisProcessServiceConfig extends ServiceConfig {
	
	@Config(key = "refresh.interval", description = "Refresh interval of processes")
	public Optional<Integer> getRefreshInterval();
	
	@Config(key = "refresh.enabled", description = "Refresh enable flag")
	public Boolean getRefreshEnabled(Boolean defaultValue);
}
