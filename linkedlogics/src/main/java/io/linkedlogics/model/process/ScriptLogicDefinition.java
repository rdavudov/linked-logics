package io.linkedlogics.model.process;

import io.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScriptLogicDefinition extends BaseLogicDefinition {
	private ExpressionLogicDefinition expression;
	private String returnAs;
	private boolean returnAsMap;
	
	public ScriptLogicDefinition() {
		super(ProcessLogicTypes.SCRIPT);
	}
	
	public ScriptLogicDefinition clone() {
		return null;
	}

	public static class ScriptLogicBuilder extends BaseLogicBuilder<ScriptLogicBuilder, ScriptLogicDefinition> {
		public ScriptLogicBuilder(ExpressionLogicDefinition expression) {
			super(new ScriptLogicDefinition());
			getLogic().setExpression(expression);
		}
		
		public ScriptLogicBuilder returnAs(String returnAs) {
			this.getLogic().setReturnAs(returnAs);
			return this;
		}
		
		public ScriptLogicBuilder returnAsMap() {
			this.getLogic().setReturnAsMap(true);
			return this;
		}

		@Override
		public ScriptLogicDefinition build() {
			return super.build();
		}
	}
}
