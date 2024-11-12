package io.linkedlogics.redis.service.config;

import java.util.Optional;

import io.linkedlogics.service.config.Config;
import io.linkedlogics.service.config.Prefix;
import io.linkedlogics.service.config.ServiceConfig;

@Prefix("redis")
public interface RedisConnectionServiceConfig extends ServiceConfig {
	
	@Config(key = "host", description = "Redis host", required = true)
	public String getHost();
	
	@Config(key = "port", description = "Redis port", required = true)
	public Integer getPort();
	
	@Config(key = "password", description = "Redis password")
	public Optional<String> getPassword();
	
	@Config(key = "timeout", description = "Redis timeout")
	public Optional<Integer> getTimeout();
	
}
