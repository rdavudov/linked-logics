package io.linkedlogics.model.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.linkedlogics.model.ProcessLogicTypes;
import io.linkedlogics.model.process.ErrorLogicDefinition.ErrorLogicBuilder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class BaseLogicDefinition extends TypedLogicDefinition implements Cloneable {
	@JsonIgnore
	protected String position;
	protected Boolean disabled = Boolean.FALSE;
	protected Map<String, Object> inputs = new HashMap<>();
	protected Map<String, Object> outputs = new HashMap<>();
	protected ForkLogicDefinition fork;
	protected JoinLogicDefinition join;
	protected Boolean forced = Boolean.FALSE;
	protected RetryLogicDefinition retry;
	protected DelayLogicDefinition delay;
	protected List<ErrorLogicDefinition> errors = new ArrayList<>();
	protected TimeoutLogicDefinition timeout;
	protected LabelLogicDefinition label;
	protected String name;
	protected String desc;
	
	public BaseLogicDefinition(ProcessLogicTypes type) {
		super(type);
	}
	
	public final BaseLogicDefinition cloneLogic() {
		BaseLogicDefinition clone = clone();
		clone.setDisabled(getDisabled());
		clone.setFork(fork != null ? fork.cloneLogic() : null);
		clone.setJoin(join != null ? join.cloneLogic() : null);
		clone.setForced(getForced());
		clone.setRetry(retry != null ? retry.cloneLogic() : null);
		clone.setDelay(delay != null ? delay.cloneLogic() : null);
		clone.setErrors(errors != null ? errors.stream().map(e -> e.cloneLogic()).toList() : null);
		clone.getInputs().putAll(getInputs());
		clone.getOutputs().putAll(getOutputs());
		clone.setTimeout(timeout != null ? timeout.cloneLogic() : null);
		clone.setLabel(label != null ? label.cloneLogic() : null);
		return clone;
	}
	
	protected abstract BaseLogicDefinition clone() ;
	
	@Getter
	public static class BaseLogicBuilder<T, L extends BaseLogicDefinition> {
		protected L logic;
		
		public BaseLogicBuilder(L logic) {
			this.logic = logic;
		}
		
		public T disabled() {
			this.logic.setDisabled(true);
			return (T) this;
		}
		
		public T input(String key, Object value) {
			this.logic.getInputs().put(key, value);
			return (T) this;
		}
		
		public T inputs(Object... inputs) {
			if (inputs.length % 2 == 0) {
				IntStream.range(0, inputs.length / 2).forEach(i -> {
					this.logic.getInputs().put((String) inputs[i * 2], inputs[i * 2 + 1]);
				});
				return (T) this;
			} else {
				throw new IllegalArgumentException("parameters size must be even");
			}
		}
		
		public T inputs(Map<String, Object> inputs) {
			this.logic.getInputs().putAll(inputs);
			return (T) this;
		}
		
		public T join(String... join) {
			this.logic.setJoin(new JoinLogicDefinition(join));
			return (T) this;
		}
		
		public T forced() {
			this.logic.setForced(true);
			return (T) this;
		}
		
		public T delayed(int delay) {
			this.logic.setDelay(new DelayLogicDefinition(delay));
			return (T) this;
		}
		
		public T handle(ErrorLogicBuilder... errors) {
			for(ErrorLogicBuilder error : errors) {
				this.logic.getErrors().add(error.build());
			}
			return (T) this;
		}
		
		public T timeout(int timeout) {
			this.logic.setTimeout(new TimeoutLogicDefinition(timeout));
			return (T) this;
		}
		
		public T label(String label) {
			this.logic.setLabel(new LabelLogicDefinition(label));
			return (T) this;
		}
		
		public T name(String name) {
			this.logic.setName(name);
			return (T) this;
		}
		
		public T desc(String desc) {
			this.logic.setDesc(desc);
			return (T) this;
		}
		
		public L build() {
			return (L) logic;
		}
	}
}
