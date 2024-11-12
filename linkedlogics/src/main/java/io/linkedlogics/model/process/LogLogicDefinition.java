package io.linkedlogics.model.process;

import io.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogLogicDefinition extends BaseLogicDefinition {
	public enum Level {
		TRACE, DEBUG, INFO, WARN, ERROR
	}
	
	private String message;
	private Level level = Level.INFO;
	
	public LogLogicDefinition(String message) {
		super(ProcessLogicTypes.LOG);
		this.message = message;
	}
	
	public LogLogicDefinition clone() {
		LogLogicDefinition clone = new LogLogicDefinition(getMessage());
		return clone;
	}
	
	public static class LogLogicBuilder extends BaseLogicBuilder<LogLogicBuilder, LogLogicDefinition> {
		public LogLogicBuilder(String message) {
			super(new LogLogicDefinition(message));
		}
		
		public LogLogicBuilder level(Level level) {
			this.getLogic().setLevel(level);
			return this;
		}
	}
}
