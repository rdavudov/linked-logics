package io.linkedlogics.service.local.config;

import io.linkedlogics.service.config.Config;
import io.linkedlogics.service.config.Prefix;
import io.linkedlogics.service.config.ServiceConfig;

@Prefix("services.processor")
public interface LocalProcessorServiceConfig extends ServiceConfig {

	@Config(key = "threads", description = "Number of threads to execute tasks")
	public Integer getThreads(int defaultValue);
	
	@Config(key = "timeout", description = "Timeout for executing logic")
	public Integer getTimeout();
	
	@Config(key = "max-blocking", description = "Max blocking time for a thread")
	public Integer getMaxBlocking(int defaultValue);
	
	@Config(key = "bypass", description = "Skips messaging when next logic is also on same application")
	public Boolean getBypass(Boolean defaultValue);
	
	@Config(key = "capacity", description = "Task backlog capacity")
	public Integer getCapacity(int defaultValue);
}
