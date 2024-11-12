package io.linkedlogics.model.process;

import io.linkedlogics.model.ProcessLogicTypes;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DelayLogicDefinition extends TypedLogicDefinition {
	private int seconds;
	
	public DelayLogicDefinition(int seconds) {
		super(ProcessLogicTypes.DELAY);
		this.seconds = seconds;
	}
	
	public DelayLogicDefinition cloneLogic() {
		return new DelayLogicDefinition(getSeconds());
	}
}
