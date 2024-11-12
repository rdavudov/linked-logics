package io.linkedlogics.model.process;

import java.util.List;
import java.util.stream.IntStream;

import io.linkedlogics.model.ProcessLogicTypes;
import io.linkedlogics.model.process.ErrorLogicDefinition.ErrorLogicBuilder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HandleLogicDefinition extends TypedLogicDefinition {
	private List<ErrorLogicDefinition> errors;
	
	public HandleLogicDefinition() {
		super(ProcessLogicTypes.ERROR);
	}
	
	public HandleLogicDefinition cloneLogic() {
		HandleLogicDefinition clone = new HandleLogicDefinition();
		clone.setErrors(errors.stream().map(e -> e.cloneLogic()).toList());
		return clone;
	}
	
	@Getter
	public static class HandleLogicBuilder {
		private HandleLogicDefinition logic;
		
		public HandleLogicBuilder(ErrorLogicBuilder... errors) {
			logic = new HandleLogicDefinition();
			logic.setErrors(IntStream.range(0, errors.length).boxed().map(i -> errors[i].build()).toList());
		}
		
		public HandleLogicDefinition build() {
			return logic;
		}
	}
}
