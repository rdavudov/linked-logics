package io.linkedlogics.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.BaseLogicDefinition.BaseLogicBuilder;
import io.linkedlogics.model.process.helper.LogicPositioner;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessDefinition implements Comparable<ProcessDefinition> {
	public static final int LATEST_VERSION = -1;
	
	private String id;
	private int version = LATEST_VERSION;
	private Map<String, Object> inputs = new HashMap<>();
	private boolean archived;
	
	private List<BaseLogicDefinition> logics = new ArrayList<BaseLogicDefinition>();
	@JsonIgnore
	private Map<String, BaseLogicDefinition> positions;
	@JsonIgnore
	private Map<String, BaseLogicDefinition> labels;
	
	public Optional<BaseLogicDefinition> getLogicByPosition(String position) {
		return Optional.ofNullable(positions.get(position));
	}
	
	public List<BaseLogicDefinition> cloneLogics() {
		return logics.stream().map(l -> l.cloneLogic()).collect(Collectors.toList());
	}
	
	@Override
	public int compareTo(ProcessDefinition o) {
		if (this.getId().equals(o.getId())) {
			return this.getVersion() - o.getVersion();
		}
		return this.getId().compareTo(o.getId());
	}
	
	public static class ProcessBuilder {
		private ProcessDefinition process;
		
		public ProcessBuilder() {
			this.process = new ProcessDefinition();
		}
		
		public ProcessBuilder id(String id) {
			this.process.setId(id);
			return this;
		}
		
		public ProcessBuilder version(int version) {
			this.process.setVersion(version);
			return this;
		}
		
		public ProcessBuilder archived() {
			this.process.setArchived(true);
			return this;
		}
		
		public ProcessBuilder add(BaseLogicBuilder<?, ?> logic) {
			this.process.getLogics().add(logic.build());
			return this;
		}
		
		public ProcessBuilder add(List<BaseLogicBuilder<?, ?>> logics) {
			logics.forEach(l -> this.process.getLogics().add(l.build()));
			return this;
		}
		
		public ProcessBuilder input(String key, Object value) {
			this.process.getInputs().put(key, value);
			return this;
		}
		
		public ProcessBuilder inputs(Object... inputs) {
			if (inputs.length % 2 == 0) {
				IntStream.range(0, inputs.length / 2).forEach(i -> {
					this.process.getInputs().put((String) inputs[i * 2], inputs[i * 2 + 1]);
				});
				return this;
			} else {
				throw new IllegalArgumentException("parameters size must be even");
			}
		}
		
		public ProcessBuilder inputs(Map<String, Object> inputs) {
			this.process.getInputs().putAll(inputs);
			return this;
		}
		
		public ProcessDefinition build() {
			LogicPositioner.setPositions(this.process);
			return this.process;
		}
	}
}

	