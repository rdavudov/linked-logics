package io.linkedlogics.service.handler.process;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.ExpressionLogicDefinition;
import io.linkedlogics.service.EvaluatorService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class OutputFlowHandler extends ProcessFlowHandler {
	public OutputFlowHandler() {

	}

	public OutputFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent()
				&& (context.getError() == null || (candidate.get().getForced() != null && candidate.get().getForced())) 
					&& candidatePosition.equals(context.getLogicPosition())) {
			
			if (context.getOutput() != null) {
				trace(context, "adding outputs to context " + context.getOutput(), candidatePosition, Flow.CONTINUE);
				context.getOutput().entrySet().forEach(e -> context.getParams().put(e.getKey(), e.getValue()));
			} else {
				trace(context, "nothing to add", candidatePosition, Flow.CONTINUE);
			}
			
			if (!candidate.get().getOutputs().isEmpty()) {
				final StringBuilder outputs = new StringBuilder();
				final EvaluatorService evaluator = ServiceLocator.getInstance().getEvaluatorService();
				candidate.get().getOutputs().entrySet().stream().forEach(e -> {
					if (e.getValue() instanceof ExpressionLogicDefinition) {
						context.getParams().put(e.getKey(), evaluator.evaluate(((ExpressionLogicDefinition) e.getValue()).getExpression(), context.getParams()));
					} else {
						context.getParams().put(e.getKey(), e.getValue());
					}
					outputs.append(",").append(e.getKey());
				});
				
				trace(context, "adding " + outputs.toString().substring(1) + " to context", candidatePosition, Flow.CONTINUE);
			}
		}
		
		return super.handle(candidate, candidatePosition, context);
	}
}
