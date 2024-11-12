package io.linkedlogics.model.process;

import io.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;

@Getter
public class ExitLogicDefinition extends BaseLogicDefinition {
	
	public ExitLogicDefinition() {
		super(ProcessLogicTypes.EXIT);
	}
	
	public ExitLogicDefinition clone() {
		ExitLogicDefinition clone = new ExitLogicDefinition();
		return clone;
	}
	
	public static class ExitLogicBuilder extends BaseLogicBuilder<ExitLogicBuilder, ExitLogicDefinition> {
		public ExitLogicBuilder() {
			super(new ExitLogicDefinition());
		}
	}
}
