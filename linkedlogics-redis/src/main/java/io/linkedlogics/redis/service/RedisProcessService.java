package io.linkedlogics.redis.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.ProcessDefinitionReader;
import io.linkedlogics.model.ProcessDefinitionWriter;
import io.linkedlogics.model.process.helper.LogicDependencies;
import io.linkedlogics.redis.repository.ProcessRepository;
import io.linkedlogics.redis.service.config.RedisProcessServiceConfig;
import io.linkedlogics.service.ProcessService;
import io.linkedlogics.service.config.ServiceConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisProcessService implements ProcessService {
	protected Map<String, ProcessDefinition> definitions = new ConcurrentHashMap<>();
	private ScheduledExecutorService service;
	private ProcessRepository repository;
	private RedisProcessServiceConfig config = new ServiceConfiguration().getConfig(RedisProcessServiceConfig.class);

	public RedisProcessService() {
		this.repository = new ProcessRepository(new RedisConnectionService().getRedisTemplate());
	}

	@Override
	public void start() {
		if (config.getRefreshEnabled(true)) {
			service = Executors.newSingleThreadScheduledExecutor();
			service.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					refreshProcesses();
				}
			}, config.getRefreshInterval().get(), config.getRefreshInterval().get(), TimeUnit.SECONDS);
		}
	}

	@Override
	public void stop() {
		if (service != null) {
			service.shutdownNow();
		}
	}
	
	@Override
	public Optional<ProcessDefinition> getProcess(String processId) {
		return getProcess(processId, LATEST_VERSION);
	}

	@Override
	public Optional<ProcessDefinition> getProcess(String processId, int processVersion) {
		try {
			int version;
			if (processVersion == LATEST_VERSION) {
				version = repository.getMaxVersion(processId).map(Function.identity()).orElseThrow(() -> new RuntimeException("Get max version failed"));
			} else {
				version = processVersion;
			}
			
			Optional<ProcessDefinition> process = Optional.ofNullable(definitions.get(getProcessKey(processId, version)));

			if (process.isEmpty()) {
				Optional<ProcessDefinition> newProcess = repository.get(processId, version);
				if (newProcess.isPresent()) {
					addProcessToMap(newProcess.get());
					return Optional.of(newProcess.get());
				}
			} else {
				return process;
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return Optional.empty();
	}

	@Override
	public void addProcess(ProcessDefinition process) {
		if (!process.isArchived()) {
			addProcessToMap(process);
			try {
				repository.set(process);
				repository.setVersion(process.getId(), process.getVersion());
			} catch (Exception e) {
				throw new RuntimeException(String.format("unable to store process %s[%d] in redis", process.getId(), process.getVersion()), e);
			}
		} else {
			try {
				repository.deleteVersion(process.getId(), process.getVersion());
				repository.delete(process.getId(), process.getVersion());
			} catch (Exception e) {
				throw new RuntimeException(String.format("unable to delete process %s[%d] in redis", process.getId(), process.getVersion()), e);
			}
		}
	}
	
	public void addProcessToMap(ProcessDefinition process) {
		if (process.isArchived()) {
			log.info(String.format("process %s:%d is archived", process.getId(), process.getVersion()));
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

	public void refreshProcesses() {
		try {
			for (ProcessDefinition process : definitions.values()) {
				try {
					repository.get(process.getId(), process.getVersion()).ifPresent(p -> {
						addProcessToMap(p);
					});
				} catch (Exception e) {
					log.error(String.format("unable to read process %s:%d", process.getId(), process.getVersion()), e);
				}
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
}
