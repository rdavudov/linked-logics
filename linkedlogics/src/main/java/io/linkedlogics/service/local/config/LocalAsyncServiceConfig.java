package io.linkedlogics.service.local.config;

import io.linkedlogics.service.config.Config;
import io.linkedlogics.service.config.Prefix;
import io.linkedlogics.service.config.ServiceConfig;

@Prefix("services.async")
public interface LocalAsyncServiceConfig extends ServiceConfig {

	@Config(key = "expire-time", description = "Async response expiration time")
	public Integer getExpireTimeOrDefault(Integer defaultValue) ;
}
