package io.linkedlogics.model.process;

import java.util.Optional;

import io.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForkLogicDefinition extends TypedLogicDefinition {
	private Optional<String> key;
	
	public ForkLogicDefinition(String key) {
		super(ProcessLogicTypes.FORK);
		this.key = Optional.of(key);
	}
	
	public ForkLogicDefinition() {
		super(ProcessLogicTypes.FORK);
		this.key = Optional.empty();
	}
	
	public ForkLogicDefinition cloneLogic() {
		ForkLogicDefinition clone = new ForkLogicDefinition();
		clone.setKey(getKey());
		return clone;
	}
}
