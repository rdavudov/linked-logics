package io.linkedlogics.model.process;

import java.util.HashSet;
import java.util.Set;

import io.linkedlogics.model.ProcessLogicTypes;
import io.linkedlogics.model.process.BaseLogicDefinition.BaseLogicBuilder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorLogicDefinition extends TypedLogicDefinition {
	private BaseLogicDefinition errorLogic;
	private Set<Integer> errorCodeSet = new HashSet<>();
	private Set<String> errorMessageSet = new HashSet<>();
	private boolean throwAgain;
	private Integer throwErrorCode;
	private String throwErrorMessage;
	
	public ErrorLogicDefinition() {
		super(ProcessLogicTypes.ERROR);
	}
	
	public ErrorLogicDefinition cloneLogic() {
		ErrorLogicDefinition clone = new ErrorLogicDefinition();
		clone.setErrorCodeSet(new HashSet<>(getErrorCodeSet()));
		clone.setErrorMessageSet(new HashSet<>(getErrorMessageSet()));
		clone.setErrorLogic(errorLogic != null ? errorLogic.cloneLogic() : null);
		clone.setThrowAgain(isThrowAgain());
		clone.setThrowErrorCode(getThrowErrorCode());
		clone.setThrowErrorMessage(getThrowErrorMessage());
		return clone;
	}
	
	@Getter
	public static class ErrorLogicBuilder {
		private ErrorLogicDefinition logic;
		
		public ErrorLogicBuilder() {
			logic = new ErrorLogicDefinition();
		}
		
		public ErrorLogicBuilder withCodes(Integer... errorCodes) {
			this.getLogic().setErrorCodeSet(Set.of(errorCodes));
			return this;
		}
		
		public ErrorLogicBuilder withMessages(String... errorMessages) {
			this.getLogic().setErrorMessageSet(Set.of(errorMessages));
			return this;
		}
		
		public ErrorLogicBuilder orMessages(String... errorMessages) {
			return withMessages(errorMessages);
		}
		
		public ErrorLogicBuilder using(BaseLogicBuilder<?, ?> errorLogic) {
			this.getLogic().setErrorLogic(errorLogic.build());
			return this;
		}
		
		public ErrorLogicBuilder throwAgain() {
			this.getLogic().setThrowAgain(true);
			return this;
		}
		
		public ErrorLogicBuilder throwAgain(int errorCode, String errorMessage) {
			this.getLogic().setThrowAgain(true);
			this.getLogic().setThrowErrorCode(errorCode);
			this.getLogic().setThrowErrorMessage(errorMessage);
			return this;
		}
		
		public ErrorLogicDefinition build() {
			return logic;
		}
	}
}
