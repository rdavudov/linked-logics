package io.linkedlogics.service.local.config;

import io.linkedlogics.service.config.Config;
import io.linkedlogics.service.config.Prefix;
import io.linkedlogics.service.config.ServiceConfig;

@Prefix("services.callback")
public interface LocalCallbackServiceConfig extends ServiceConfig {

	@Config(key = "expire-time", description = "Callback expiration time")
	public Integer getExpireTimeOrDefault(Integer defaultValue) ;
}
