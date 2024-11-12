package io.linkedlogics.model.process;

import java.util.Arrays;
import java.util.stream.Collectors;

import io.linkedlogics.context.Context;
import io.linkedlogics.model.ProcessLogicTypes;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.config.ServiceConfiguration;
import io.linkedlogics.service.local.config.LocalProcessServiceConfig;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoopLogicDefinition extends GroupLogicDefinition {
	private ExpressionLogicDefinition expression;
	
	public LoopLogicDefinition() {
		super(ProcessLogicTypes.LOOP);
	}
	
	public boolean isSatisfied(Context context) {
		return (Boolean) ServiceLocator.getInstance().getEvaluatorService().evaluate(expression.getExpression(), context.getParams());
	}
	
	public LoopLogicDefinition clone() {
		LoopLogicDefinition clone = new LoopLogicDefinition();
		clone.setExpression(getExpression().cloneLogic());
		clone.setLogics(this.getLogics().stream().map(l -> l.clone()).collect(Collectors.toList()));
		return clone;
	}

	public static class LoopLogicBuilder extends BaseLogicBuilder<LoopLogicBuilder, LoopLogicDefinition> {
		public LoopLogicBuilder(ExpressionLogicDefinition expression, BaseLogicBuilder... logics) {
			super(new LoopLogicDefinition());
			getLogic().setExpression(expression);
			this.getLogic().addLogics(Arrays.stream(logics).map(b -> b.build()).collect(Collectors.toList()));
		}
		
		public LoopLogicBuilder fork(String fork) {
			this.getLogic().setFork(new ForkLogicDefinition(fork));
			return this;
		}
		
		public LoopLogicBuilder fork() {
			this.getLogic().setFork(new ForkLogicDefinition());
			return this;
		}
		
		@Override
		public LoopLogicDefinition build() {
			this.getLogic().getLogics().forEach(l -> {
				this.getLogic().getInputs().entrySet().forEach(e -> {
					if (new ServiceConfiguration().getConfig(LocalProcessServiceConfig.class).getParentInputsHasPriority().orElse(Boolean.FALSE)) {
						l.getInputs().put(e.getKey(), e.getValue());
					} else {
						l.getInputs().putIfAbsent(e.getKey(), e.getValue());
					}
				});
			});
			return super.build();
		}
	}
}
