package io.linkedlogics.model.process;

import java.util.stream.Collectors;

import io.linkedlogics.model.ProcessLogicTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ProcessLogicDefinition extends GroupLogicDefinition {
	private String processId;
	private int version;
	
	public ProcessLogicDefinition() {
		super(ProcessLogicTypes.PROCESS);
	}

	public ProcessLogicDefinition clone() {
		ProcessLogicDefinition clone = new ProcessLogicDefinition(getProcessId(), getVersion());
		if (!getLogics().isEmpty()) {
			clone.setLogics(getLogics().stream().map(l -> l.clone()).collect(Collectors.toList()));
		}
		return clone;
	}

	public String toString() {
		return "process ";
	}

	public static class ProcessLogicBuilder extends BaseLogicBuilder<ProcessLogicBuilder, ProcessLogicDefinition> {
		public ProcessLogicBuilder(String processId, int version) {
			super(new ProcessLogicDefinition(processId, version));
		}

		@Override
		public ProcessLogicDefinition build() {
			return super.build();
		}
	}
}
