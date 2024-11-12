package io.linkedlogics.context;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.exception.MissingLogicError;
import io.linkedlogics.model.process.ExpressionLogicDefinition;
import io.linkedlogics.model.process.ProcessLogicDefinition;
import io.linkedlogics.model.process.helper.LogicFinder;
import io.linkedlogics.service.EvaluatorService;
import io.linkedlogics.service.ProcessService;
import io.linkedlogics.service.ServiceLocator;

public class ContextBuilder {
	private String processId;
	private int processVersion;
	private String contextId;
	private String contextKey;
	private String origin = LinkedLogics.getInstanceName();
	private Map<String, Object> params = new HashMap<>();
	
	private ContextBuilder(String processId, int processVersion) {
		this.processId = processId;
		this.processVersion = processVersion;
	}
	
	public static ContextBuilder newContext(String processId) {
		return new ContextBuilder(processId, ProcessService.LATEST_VERSION);
	}
	
	public static ContextBuilder newContext(String processId, int processVersion) {
		return new ContextBuilder(processId, processVersion);
	}
	
	public Context build() {
		return ServiceLocator.getInstance().getProcessService().getProcess(processId, processVersion).map(process -> {
			LogicFinder.findLogics(process, ProcessLogicDefinition.class).stream()
			.map(l -> (ProcessLogicDefinition) l)
			.filter(p -> p.getLogics().isEmpty())
			.findAny().map(l -> {
				throw new MissingLogicError(l.getProcessId(), l.getVersion());
			});
			
			if (!process.getInputs().isEmpty()) {
				final EvaluatorService evaluator = ServiceLocator.getInstance().getEvaluatorService();
				process.getInputs().entrySet().stream().forEach(e -> {
					if (e.getValue() instanceof ExpressionLogicDefinition) {
						params.put(e.getKey(), evaluator.evaluate(((ExpressionLogicDefinition) e.getValue()).getExpression(), params));
					} else {
						params.put(e.getKey(), e.getValue());
					}
				});
			}
			
			return new Context(contextId, contextKey, process.getId(), process.getVersion(), params, origin);
		}).orElseThrow(() -> new IllegalArgumentException(String.format("process %s[%d] is not found", processId, processVersion)));
	}
	
	public ContextBuilder id(String id) {
		this.contextId = id;
		return this;
	}
	
	public ContextBuilder key(String key) {
		this.contextKey = key;
		return this;
	}
	
	public ContextBuilder origin(String origin) {
		this.origin = origin;
		return this;
	}
	
	public ContextBuilder param(String key, Object value) {
		this.params.put(key, value);
		return this;
	}
	
	public ContextBuilder params(Object... inputs) {
		if (inputs.length % 2 == 0) {
			IntStream.range(0, inputs.length / 2).forEach(i -> {
				this.params.put((String) inputs[i * 2], inputs[i * 2 + 1]);
			});
			return this;
		} else {
			throw new IllegalArgumentException("parameters size must be even");
		}
	}
	
	public ContextBuilder params(Map<String, Object> inputs) {
		this.params.putAll(inputs);
		return this;
	}
}
