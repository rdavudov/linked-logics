package io.linkedlogics.jdbc.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import io.linkedlogics.jdbc.repository.ProcessRepository;
import io.linkedlogics.jdbc.repository.ProcessRepository.ProcessEntity;
import io.linkedlogics.jdbc.service.config.JdbcProcessServiceConfig;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.ProcessDefinitionReader;
import io.linkedlogics.model.ProcessDefinitionWriter;
import io.linkedlogics.model.process.helper.LogicDependencies;
import io.linkedlogics.service.ProcessService;
import io.linkedlogics.service.config.ServiceConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdbcProcessService implements ProcessService {
	protected Map<String, ProcessDefinition> definitions = new ConcurrentHashMap<>();
	private ScheduledExecutorService service;
	private ProcessRepository repository;
	private JdbcProcessServiceConfig config = new ServiceConfiguration().getConfig(JdbcProcessServiceConfig.class);

	public JdbcProcessService() {
		this.repository = new ProcessRepository(new JdbcConnectionService().getDataSource());
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
				version = repository.getMaxVersion(processId).map(Function.identity()).orElseThrow(() -> new RuntimeException());
			} else {
				version = processVersion;
			}

			Optional<ProcessDefinition> process = Optional.ofNullable(definitions.get(getProcessKey(processId, version)));

			if (process.isEmpty()) {
				Optional<ProcessEntity> entity = repository.getProcess(processId, version);
				if (entity.isPresent()) {
					ProcessDefinition newProcess = new ProcessDefinitionReader(entity.get().getBuilder()).read();
					addProcessToMap(newProcess);
					return Optional.of(newProcess);
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
		addProcessToMap(process);

		try {
			String builder = new ProcessDefinitionWriter(process).write();

			if (repository.getProcess(process.getId(), process.getVersion()).isPresent()) {
				repository.update(new ProcessEntity(process.getId(), process.getVersion(), builder));
			} else {
				repository.createProcess(new ProcessEntity(process.getId(), process.getVersion(), builder));
			}
		} catch (Exception e) {
			throw new RuntimeException(String.format("unable to store process %s[%d] in db", process.getId(), process.getVersion()));
		}
	}

	public void addProcessToMap(ProcessDefinition process) {
		if (process.isArchived()) {
			log.info(String.format("process %s:%d is archived", process.getId(), process.getVersion()));
			return;
		}
		
		ProcessDefinition validatedDefinition = new ProcessDefinitionReader(new ProcessDefinitionWriter(process).write()).read();
		
		if (definitions.containsKey(getProcessKey(validatedDefinition))) {
			log.warn("process {}:{} was overwritten", process.getId(), process.getVersion());
		}
		
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
			List<ProcessEntity> entities = repository.getProcesses();

			for (ProcessEntity entity : entities) {
				ProcessDefinitionReader reader = new ProcessDefinitionReader(entity.getBuilder());
				try {
					ProcessDefinition process = reader.read();
					addProcessToMap(process);
				} catch (Exception e) {
					log.error(String.format("unable to read process %s:%d", entity.getId(), entity.getVersion()), e);
				}
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
}
