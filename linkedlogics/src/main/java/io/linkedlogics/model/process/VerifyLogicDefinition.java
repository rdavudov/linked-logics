package io.linkedlogics.model.process;

import io.linkedlogics.context.Context;
import io.linkedlogics.model.ProcessLogicTypes;
import io.linkedlogics.service.ServiceLocator;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerifyLogicDefinition extends BaseLogicDefinition {
	private ExpressionLogicDefinition expression;
	private Integer errorCode = -1;
	private String errorMessage;
	
	public VerifyLogicDefinition() {
		super(ProcessLogicTypes.VERIFY);
	}
	
	public boolean isVerified(Context context) {
		return (Boolean) ServiceLocator.getInstance().getEvaluatorService().evaluate(expression.getExpression(), context.getParams());
	}
	
	public VerifyLogicDefinition clone() {
		VerifyLogicDefinition clone = new VerifyLogicDefinition();
		clone.setExpression(getExpression().cloneLogic());
		clone.setErrorCode(getErrorCode());
		clone.setErrorMessage(getErrorMessage());
		return clone;
	}

	public static class VerifyLogicBuilder extends BaseLogicBuilder<VerifyLogicBuilder, VerifyLogicDefinition> {
		public VerifyLogicBuilder(ExpressionLogicDefinition expression) {
			super(new VerifyLogicDefinition());
			getLogic().setExpression(expression);
		}
		
		public VerifyLogicBuilder elseFailWithCode(int errorCode) {
			this.getLogic().setErrorCode(errorCode);
			return this;
		}

		public VerifyLogicBuilder andCode(int errorCode) {
			this.getLogic().setErrorCode(errorCode);
			return this;
		}
		
		@Deprecated
		public VerifyLogicBuilder code(int errorCode) {
			this.getLogic().setErrorCode(errorCode);
			return this;
		}
		
		public VerifyLogicBuilder elseFailWithMessage(String errorMessage) {
			this.getLogic().setErrorMessage(errorMessage);
			return this;
		}
		
		public VerifyLogicBuilder andMessage(String errorMessage) {
			this.getLogic().setErrorMessage(errorMessage);
			return this;
		}
		
		@Deprecated
		public VerifyLogicBuilder message(String errorMessage) {
			this.getLogic().setErrorMessage(errorMessage);
			return this;
		}
	}
}
