package io.linkedlogics.service.local.config;


import java.util.Optional;

import io.linkedlogics.service.config.Config;
import io.linkedlogics.service.config.Prefix;
import io.linkedlogics.service.config.ServiceConfig;

@Prefix("services.process")
public interface LocalProcessServiceConfig extends ServiceConfig {
	
	@Config(key = "parent-inputs-has-priority", description = "Inputs coming from parent items overwrite")
	public Optional<Boolean> getParentInputsHasPriority();
}

