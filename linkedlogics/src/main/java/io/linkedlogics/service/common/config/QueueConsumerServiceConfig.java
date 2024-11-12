package io.linkedlogics.service.common.config;

import java.util.Optional;

import io.linkedlogics.service.config.Config;
import io.linkedlogics.service.config.Prefix;
import io.linkedlogics.service.config.ServiceConfig;

@Prefix("services.consumer")
public interface QueueConsumerServiceConfig extends ServiceConfig {
	
	@Config(key = "delay", description = "Sleep time when no messages")
	public Integer getDelay(Integer defaultValue);
	
	@Config(key = "rate.limit", description = "rate limit")
	public Optional<Long> getRateLimit();
	
	@Config(key = "rate.interval", description = "rate limit interval")
	public String getRateInterval(String defaultValue);
}
