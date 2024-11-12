package io.linkedlogics.model.process;

import io.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FailLogicDefinition extends BaseLogicDefinition {
	private Integer errorCode;
	private String errorMessage;
	
	public FailLogicDefinition() {
		super(ProcessLogicTypes.FAIL);
	}
	
	public FailLogicDefinition clone() {
		FailLogicDefinition clone = new FailLogicDefinition();
		return clone;
	}
	
	public static class FailLogicBuilder extends BaseLogicBuilder<FailLogicBuilder, FailLogicDefinition> {
		public FailLogicBuilder() {
			super(new FailLogicDefinition());
		}
		
		public FailLogicBuilder withCode(int errorCode) {
			this.getLogic().setErrorCode(errorCode);
			return this;
		}
		
		public FailLogicBuilder withMessage(String errorMessage) {
			this.getLogic().setErrorMessage(errorMessage);
			return this;
		}
		
		public FailLogicBuilder andMessage(String errorMessage) {
			return withMessage(errorMessage);
		}
	}
}
