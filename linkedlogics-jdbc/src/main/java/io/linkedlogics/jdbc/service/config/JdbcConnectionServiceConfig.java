package io.linkedlogics.jdbc.service.config;

import java.util.Optional;

import io.linkedlogics.service.config.Config;
import io.linkedlogics.service.config.Prefix;
import io.linkedlogics.service.config.ServiceConfig;

@Prefix("jdbc")
public interface JdbcConnectionServiceConfig extends ServiceConfig {
	
	@Config(key = "url", description = "Database url", required = true)
	public String getUrl();
	
	@Config(key = "username", description = "Database username", required = true)
	public String getUsername();
	
	@Config(key = "password", description = "Database password", required = true)
	public String getPassword();
	
	@Config(key = "driver", description = "Database driver", required = true)
	public String getDriver();
	
	@Config(key = "pool.min", description = "Connection pool min", required = false)
	public Optional<Integer> getPoolMin();
	
	@Config(key = "pool.max", description = "Connection pool max", required = false)
	public Optional<Integer> getPoolMax();
	
}
