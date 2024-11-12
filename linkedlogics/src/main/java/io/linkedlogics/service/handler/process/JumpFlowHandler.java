package io.linkedlogics.service.handler.process;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextError;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.context.ContextError.ErrorType;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.JumpLogicDefinition;
import io.linkedlogics.service.ServiceLocator;

public class JumpFlowHandler extends ProcessFlowHandler {
	
	public JumpFlowHandler() {
		
	}

	public JumpFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent()) {
			if (candidate.get() instanceof JumpLogicDefinition) {
				JumpLogicDefinition jump = (JumpLogicDefinition) candidate.get();
				trace(context, "jumping", candidatePosition, Flow.RESET);
				
				String targetLabel = null;
				if (jump.getTargetLabel() != null) {
					targetLabel = jump.getTargetLabel();
				} else if (jump.getTargetExpr() != null) {
					targetLabel = (String) ServiceLocator.getInstance().getEvaluatorService().evaluate(jump.getTargetExpr().getExpression(), context.getParams());
				}
				
				ProcessDefinition process = ServiceLocator.getInstance().getProcessService().getProcess(context.getProcessId(), context.getProcessVersion()).get();
				BaseLogicDefinition target = process.getLabels().get(targetLabel);

				if (target == null) {
					context.setError(new ContextError(-1, "jump failed target " + targetLabel + " not found", ErrorType.PERMANENT));
					trace(context, "not verified", candidatePosition, Flow.RESET);
					ContextFlow.jump(candidatePosition).name(candidate.get().getName()).result(Boolean.FALSE).message("jump failed NO_TARGET").log(context);
					return HandlerResult.nextCandidate(candidatePosition);
				} else if (!compareLogicPosition(candidate.get().getPosition(), target.getPosition())) {
					context.setError(new ContextError(-1, "jump failed target " + targetLabel + " contradicts with DAG", ErrorType.PERMANENT));
					trace(context, "not a DAG", candidatePosition, Flow.RESET);
					ContextFlow.jump(candidatePosition).name(candidate.get().getName()).result(Boolean.FALSE).message("jump failed NOT_DAG").log(context);
					return HandlerResult.nextCandidate(candidatePosition);
				}

				ContextFlow.jump(candidatePosition).name(candidate.get().getName()).result(Boolean.TRUE).message("jump to " + targetLabel).log(context);
				return HandlerResult.nextCandidate(target.getPosition());
			}
			trace(context, "no jump", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}