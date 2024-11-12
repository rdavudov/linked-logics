package io.linkedlogics.service.local;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;

import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.ProcessDefinitionReader;
import io.linkedlogics.model.ProcessDefinitionWriter;
import io.linkedlogics.model.process.helper.LogicDependencies;
import io.linkedlogics.service.ProcessService;
import io.linkedlogics.service.config.ServiceConfiguration;
import io.linkedlogics.service.local.config.LocalProcessServiceConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalProcessService implements ProcessService {
	protected Map<String, ProcessDefinition> definitions = new ConcurrentHashMap<>();
	private LocalProcessServiceConfig config = new ServiceConfiguration().getConfig(LocalProcessServiceConfig.class);
	
	@Override
	public Optional<ProcessDefinition> getProcess(String processId) {
		return getProcess(processId, LATEST_VERSION);
	}

	@Override
	public Optional<ProcessDefinition> getProcess(String processId, int version) {
		if (version == LATEST_VERSION) {
			OptionalInt latestVersion = definitions.values().stream().filter(p -> p.getId().equals(processId)).map(p -> p.getVersion()).mapToInt(i -> i).max();
			if (latestVersion.isPresent()) {
				return Optional.ofNullable(definitions.get(getProcessKey(processId, latestVersion.getAsInt())));
			}
		}
		return Optional.ofNullable(definitions.get(getProcessKey(processId, version)));
	}
	
	public void addProcess(ProcessDefinition process) {
		if (process.isArchived()) {
			return;
		}
		
		ProcessDefinition validatedDefinition = new ProcessDefinitionReader(new ProcessDefinitionWriter(process).write()).read();
		definitions.put(getProcessKey(validatedDefinition), validatedDefinition);
		definitions.values()
			.stream()
			.sorted((p1, p2) -> p1.compareTo(p2))
			.forEach(LogicDependencies::setDependencies);
	}
	
	protected String getProcessKey(ProcessDefinition process) {
		return String.format("%s:%d", process.getId(), process.getVersion());
	}
	
	protected String getProcessKey(String processId, int version) {
		return String.format("%s:%d", processId, version);
	}
}
