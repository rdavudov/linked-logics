package io.linkedlogics.model.process;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.linkedlogics.model.ProcessLogicTypes;
import io.linkedlogics.model.process.RetryLogicDefinition.RetryLogicBuilder;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.config.ServiceConfiguration;
import io.linkedlogics.service.local.config.LocalProcessServiceConfig;
import io.linkedlogics.service.local.config.LocalProcessorServiceConfig;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GroupLogicDefinition extends BaseLogicDefinition {
	protected List<BaseLogicDefinition> logics = new LinkedList<BaseLogicDefinition>();
	
	public GroupLogicDefinition() {
		super(ProcessLogicTypes.GROUP);
	}
	
	public GroupLogicDefinition(ProcessLogicTypes type) {
		super(type);
	}
	
	public GroupLogicDefinition clone() {
		GroupLogicDefinition clone = new GroupLogicDefinition();
		clone.setLogics(this.getLogics().stream().map(l -> l.clone()).collect(Collectors.toList()));
		return clone;
	}
	
	public String toString() {
		return "group";
	}
	
	protected void addLogics(List<BaseLogicDefinition> logics) {
		logics.forEach(l -> getLogics().add(l));
	}
	
	public static class GroupLogicBuilder extends BaseLogicBuilder<GroupLogicBuilder, GroupLogicDefinition> {
		public GroupLogicBuilder(BaseLogicBuilder... logics) {
			super(new GroupLogicDefinition());
			this.getLogic().addLogics(Arrays.stream(logics).map(b -> b.build()).collect(Collectors.toList()));
		}
		
		public GroupLogicBuilder retry(int maxRetries, int delay) {
			this.getLogic().setRetry(new RetryLogicDefinition.RetryLogicBuilder(maxRetries, delay).build());
			return this;
		}
		
		public GroupLogicBuilder retry(RetryLogicBuilder retry) {
			this.getLogic().setRetry(retry.build());
			return this;
		}
		
		public GroupLogicBuilder fork(String fork) {
			this.getLogic().setFork(new ForkLogicDefinition(fork));
			return this;
		}
		
		public GroupLogicBuilder fork() {
			this.getLogic().setFork(new ForkLogicDefinition());
			return this;
		}
		
		@Override
		public GroupLogicDefinition build() {
			this.getLogic().getLogics().forEach(l -> {
				this.getLogic().getInputs().entrySet().forEach(e -> {
					if (new ServiceConfiguration().getConfig(LocalProcessServiceConfig.class).getParentInputsHasPriority().orElse(Boolean.FALSE)) {
						l.getInputs().put(e.getKey(), e.getValue());
					} else {
						l.getInputs().putIfAbsent(e.getKey(), e.getValue());
					}
				});
			});
			return super.build();
		}
	}
}
