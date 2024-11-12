package io.linkedlogics.model.process;

import java.util.Map.Entry;

import io.linkedlogics.context.Context;
import io.linkedlogics.model.ProcessLogicTypes;
import io.linkedlogics.service.ServiceLocator;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BranchLogicDefinition extends BaseLogicDefinition {
	private ExpressionLogicDefinition expression;
	private BaseLogicDefinition leftLogic;
	private BaseLogicDefinition rightLogic;
	
	public BranchLogicDefinition() {
		super(ProcessLogicTypes.BRANCH);
	}
	
	public boolean isSatisfied(Context context) {
		return (Boolean) ServiceLocator.getInstance().getEvaluatorService().evaluate(expression.getExpression(), context.getParams());
	}
	
	public BranchLogicDefinition clone() {
		BranchLogicDefinition clone = new BranchLogicDefinition();
		clone.setExpression(getExpression().cloneLogic());
		clone.setLeftLogic(getLeftLogic().cloneLogic());
		if (getRightLogic() != null) {
			clone.setRightLogic(getRightLogic().cloneLogic());
		}
		return clone;
	}

	public static class BranchLogicBuilder extends BaseLogicBuilder<BranchLogicBuilder, BranchLogicDefinition> {
		public BranchLogicBuilder(ExpressionLogicDefinition expression, BaseLogicBuilder<?, ?> leftBranch, BaseLogicBuilder<?, ?> rightBranch) {
			super(new BranchLogicDefinition());
			getLogic().setExpression(expression);
			getLogic().setLeftLogic(leftBranch.build());
			if (rightBranch != null) {
				getLogic().setRightLogic(rightBranch.build());
			}
		}

		@Override
		public BranchLogicDefinition build() {
			for (Entry<String, Object> entry : this.getLogic().getInputs().entrySet()) {
				this.getLogic().getLeftLogic().getInputs().putIfAbsent(entry.getKey(), entry.getValue());
				if (this.getLogic().getRightLogic() != null) {
					this.getLogic().getRightLogic().getInputs().putIfAbsent(entry.getKey(), entry.getValue());
				}
			}
			return super.build();
		}
	}
}
