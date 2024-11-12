package io.linkedlogics.model.process;

import io.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;

@Getter
public class JumpLogicDefinition extends BaseLogicDefinition {
	private String targetLabel;
	private ExpressionLogicDefinition targetExpr;
	
	public JumpLogicDefinition(String targetLabel) {
		super(ProcessLogicTypes.JUMP);
		this.targetLabel = targetLabel;
	}
	
	public JumpLogicDefinition(ExpressionLogicDefinition targetExpr) {
		super(ProcessLogicTypes.JUMP);
		this.targetExpr = targetExpr;
	}
	
	public JumpLogicDefinition clone() {
		JumpLogicDefinition clone = new JumpLogicDefinition(getTargetLabel());
		return clone;
	}
	
	public static class JumpLogicBuilder extends BaseLogicBuilder<JumpLogicBuilder, JumpLogicDefinition> {
		public JumpLogicBuilder(String label) {
			super(new JumpLogicDefinition(label));
		}
		
		public JumpLogicBuilder(ExpressionLogicDefinition expr) {
			super(new JumpLogicDefinition(expr));
		}
	}
}
