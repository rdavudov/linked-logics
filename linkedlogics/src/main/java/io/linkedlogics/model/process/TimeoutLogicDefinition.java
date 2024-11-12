package io.linkedlogics.model.process;

import io.linkedlogics.model.ProcessLogicTypes;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TimeoutLogicDefinition extends TypedLogicDefinition {
	private int seconds;
	
	public TimeoutLogicDefinition(int seconds) {
		super(ProcessLogicTypes.TIMEOUT);
		this.seconds = seconds;
	}
	
	public TimeoutLogicDefinition cloneLogic() {
		return new TimeoutLogicDefinition(getSeconds());
	}
}
