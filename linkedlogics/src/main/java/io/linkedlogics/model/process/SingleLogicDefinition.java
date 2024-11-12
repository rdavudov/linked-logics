package io.linkedlogics.model.process;

import java.util.Map;
import java.util.stream.IntStream;

import io.linkedlogics.model.ProcessLogicTypes;
import io.linkedlogics.model.process.RetryLogicDefinition.RetryLogicBuilder;
import io.linkedlogics.service.LogicService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleLogicDefinition extends BaseLogicDefinition {
	private String logicId;
	private int logicVersion = LogicService.LATEST_VERSION;
	private String application;
	private String returnAs;
	private BaseLogicDefinition compensationLogic;
	
	public SingleLogicDefinition() {
		super(ProcessLogicTypes.LOGIC);
	}
	
	public SingleLogicDefinition clone() {
		SingleLogicDefinition clone = new SingleLogicDefinition();
		clone.setLogicId(getLogicId());
		clone.setLogicVersion(getLogicVersion());
		clone.getInputs().putAll(getInputs());
		return clone;
	}
	
	public static class SingleLogicBuilder extends BaseLogicBuilder<SingleLogicBuilder, SingleLogicDefinition> {
		public SingleLogicBuilder() {
			super(new SingleLogicDefinition());
		}
		
		public SingleLogicBuilder id(String id) {
			this.getLogic().setLogicId(id);
			return this;
		}
		
		public SingleLogicBuilder version(int version) {
			this.getLogic().setLogicVersion(version);
			return this;
		}
		
		public SingleLogicBuilder application(String application) {
			this.getLogic().setApplication(application);
			return this;
		}
		
		public SingleLogicBuilder returnAs(String returnAs) {
			this.getLogic().setReturnAs(returnAs);
			return this;
		}
		
		public SingleLogicBuilder compensate(SingleLogicBuilder compensationLogic) {
			this.getLogic().setCompensationLogic(compensationLogic.build());
			return this;
		}
		
		public SingleLogicBuilder fork(String fork) {
			this.getLogic().setFork(new ForkLogicDefinition(fork));
			return this;
		}
		
		public SingleLogicBuilder fork() {
			this.getLogic().setFork(new ForkLogicDefinition());
			return this;
		}
		
		public SingleLogicBuilder retry(int maxRetries, int delay) {
			this.getLogic().setRetry(new RetryLogicDefinition.RetryLogicBuilder(maxRetries, delay).build());
			return this;
		}
		
		public SingleLogicBuilder retry(RetryLogicBuilder retry) {
			this.getLogic().setRetry(retry.build());
			return this;
		}
		
		public SingleLogicBuilder output(String key, Object value) {
			this.getLogic().getOutputs().put(key, value);
			return this;
		}
		
		public SingleLogicBuilder outputs(Map<String, Object> outputs) {
			this.getLogic().getOutputs().putAll(outputs);
			return this;
		}
		
		public SingleLogicBuilder outputs(Object... outputs) {
			if (outputs.length % 2 == 0) {
				IntStream.range(0, outputs.length / 2).forEach(i -> {
					this.logic.getOutputs().put((String) outputs[i * 2], outputs[i * 2 + 1]);
				});
				return this;
			} else {
				throw new IllegalArgumentException("parameters size must be even");
			}
		}
	}
	
	public String toString() {
		return logicId;
	}
}
