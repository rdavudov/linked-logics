package io.linkedlogics.model.process;

import io.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SavepointLogicDefinition extends BaseLogicDefinition {
	
	public SavepointLogicDefinition() {
		super(ProcessLogicTypes.SAVEPOINT);
	}
	
	public SavepointLogicDefinition clone() {
		SavepointLogicDefinition clone = new SavepointLogicDefinition();
		return clone;
	}

	public static class SavepointLogicBuilder extends BaseLogicBuilder<SavepointLogicBuilder, SavepointLogicDefinition> {
		public SavepointLogicBuilder() {
			super(new SavepointLogicDefinition());
		}
	}
}
