package io.linkedlogics.service.handler.process;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.context.ContextLog;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.ExpressionLogicDefinition;
import io.linkedlogics.model.process.LogLogicDefinition;
import io.linkedlogics.model.process.LogLogicDefinition.Level;
import io.linkedlogics.service.EvaluatorService;
import io.linkedlogics.service.ServiceLocator;

public class LogFlowHandler extends ProcessFlowHandler {
	public LogFlowHandler() {

	}

	public LogFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidate.get() instanceof LogLogicDefinition) {
			LogLogicDefinition logLogic = (LogLogicDefinition) candidate.get(); 

			Map<String, Object> inputs = getInputs(context, logLogic);
			
			String message = logLogic.getMessage();
			for (Entry<String, Object> input : inputs.entrySet()) {
				message = message.replace("{" + input.getKey() + "}", input.getValue().toString());
			}
			
			ContextLog log = ContextLog.log(context).message(message);
			if (logLogic.getLevel() == Level.TRACE) {
				log.trace();
			} else if (logLogic.getLevel() == Level.DEBUG) {
				log.debug();
			} else if (logLogic.getLevel() == Level.INFO) {
				log.info();
			} else if (logLogic.getLevel() == Level.WARN) {
				log.warn();
			} else if (logLogic.getLevel() == Level.ERROR) {
				log.error();
			}
			ContextFlow.log(candidatePosition).name(candidate.get().getName()).result(Boolean.TRUE).message(message.substring(0, Math.min(message.length(), 30))).log(context);
			
			return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
	
	private Map<String, Object> getInputs(Context context, LogLogicDefinition logic) {
		final EvaluatorService evaluator = ServiceLocator.getInstance().getEvaluatorService();
		Map<String, Object> inputs = new HashMap<>();
		logic.getInputs().entrySet().stream().forEach(e -> {
			if (e.getValue() instanceof ExpressionLogicDefinition) {
				inputs.put(e.getKey(), evaluator.evaluate(((ExpressionLogicDefinition) e.getValue()).getExpression(), context.getParams()));
			} else {
				inputs.put(e.getKey(), e.getValue());
			}
		});
		return inputs;
	}
}