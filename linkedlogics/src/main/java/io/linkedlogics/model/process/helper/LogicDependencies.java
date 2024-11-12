package io.linkedlogics.model.process.helper;

import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.process.ProcessLogicDefinition;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.config.ServiceConfiguration;
import io.linkedlogics.service.local.config.LocalProcessServiceConfig;

public class LogicDependencies {
	
	public static void setDependencies(ProcessDefinition definition) {
		LogicFinder.findLogics(definition, ProcessLogicDefinition.class).stream()
		.map(l -> (ProcessLogicDefinition) l)
		.filter(p -> p.getLogics().isEmpty())
		.filter(p -> ServiceLocator.getInstance().getProcessService().getProcess(p.getProcessId(), p.getVersion()).isPresent())
		.forEach(p -> {
			p.setLogics(ServiceLocator.getInstance().getProcessService().getProcess(p.getProcessId(), p.getVersion()).get().cloneLogics());
			p.getLogics().forEach(l -> {
				p.getInputs().entrySet().forEach(e -> {
					if (new ServiceConfiguration().getConfig(LocalProcessServiceConfig.class).getParentInputsHasPriority().orElse(Boolean.FALSE)) {
						l.getInputs().put(e.getKey(), e.getValue());
					} else {
						l.getInputs().putIfAbsent(e.getKey(), e.getValue());
					}
				});
			});
			LogicPositioner.setPositions(definition);
		});
	}
}
