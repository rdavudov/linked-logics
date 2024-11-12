package io.linkedlogics.model.process;

import io.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;

@Getter
public class JoinLogicDefinition extends TypedLogicDefinition {
	private String[] key;
	
	public JoinLogicDefinition(String[] key) {
		super(ProcessLogicTypes.JOIN);
		this.key = key;
	}
	
	public JoinLogicDefinition cloneLogic() {
		JoinLogicDefinition clone = new JoinLogicDefinition(getKey());
		return clone;
	}
}
