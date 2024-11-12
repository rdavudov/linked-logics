package io.linkedlogics.model.process;

import java.util.HashSet;
import java.util.Set;

import io.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetryLogicDefinition extends TypedLogicDefinition {
	private int maxRetries;
	private int seconds;
	private Set<Integer> errorCodeSet = new HashSet<>();
	private Set<String> errorMessageSet = new HashSet<>();
	private boolean exclude;
	
	public RetryLogicDefinition() {
		super(ProcessLogicTypes.RETRY);
	}
	
	public RetryLogicDefinition cloneLogic() {
		RetryLogicDefinition clone = new RetryLogicDefinition();
		clone.setMaxRetries(getMaxRetries());
		clone.setSeconds(getSeconds());
		clone.setErrorCodeSet(getErrorCodeSet());
		clone.setErrorMessageSet(getErrorMessageSet());
		clone.setExclude(isExclude());
		return clone;
	}
	
	@Getter
	public static class RetryLogicBuilder {
		private RetryLogicDefinition logic;
		
		public RetryLogicBuilder(int maxRetries, int delay) {
			logic = new RetryLogicDefinition();
			logic.setMaxRetries(maxRetries);
			logic.setSeconds(delay);
		}
		
		public RetryLogicBuilder messages(String... errorMessages) {
			this.getLogic().setErrorMessageSet(Set.of(errorMessages));
			return this;
		}
		
		public RetryLogicBuilder include() {
			this.getLogic().setExclude(false);
			return this;
		}
		
		public RetryLogicBuilder exclude() {
			this.getLogic().setExclude(true);
			return this;
		}
		
		public RetryLogicBuilder codes(Integer... errorCodes) {
			this.getLogic().setErrorCodeSet(Set.of(errorCodes));
			return this;
		}
		
		public RetryLogicDefinition build() {
			return logic;
		}
	}
}
