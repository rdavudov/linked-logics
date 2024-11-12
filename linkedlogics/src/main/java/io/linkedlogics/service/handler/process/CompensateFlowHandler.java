package io.linkedlogics.service.handler.process;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.helper.LogicPositioner;

public class CompensateFlowHandler extends ProcessFlowHandler {
	public CompensateFlowHandler() {

	}

	public CompensateFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && context.getError() != null) {
			if (!context.getCompensables().isEmpty()) {
				String lastCompensablePosition = context.getCompensables().get(context.getCompensables().size() - 1);
				if (parentLogicPosition(lastCompensablePosition).equals(parentLogicPosition(candidatePosition))) {
					context.getCompensables().remove(context.getCompensables().size() - 1);
					if (!candidatePosition.endsWith(LogicPositioner.COMPENSATE)) {
						includeParent(candidatePosition, context);
					}
					trace(context, "compensating with next logic at " + lastCompensablePosition, candidatePosition, Flow.RESET);
					ContextFlow.compensate(lastCompensablePosition).result(Boolean.TRUE).message("compensating with logic at" + lastCompensablePosition).log(context);
					return HandlerResult.nextCandidate(lastCompensablePosition);
				}
			} else {
				trace(context, "nothing to compensate", candidatePosition, Flow.RESET);
			}
			return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
		} else {
			trace(context, "no error", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		}
	}

	private void includeParent(String candidatePosition, Context context) {
		boolean isIncluded = false;
		for (int i = context.getCompensables().size() - 1; i >= 0; i--) {
			String post = context.getCompensables().get(i);
			if (!parentLogicPosition(post).equals(parentLogicPosition(candidatePosition))) {
				context.getCompensables().add(i+1, adjacentLogicPosition(candidatePosition));
				isIncluded = true;
			}
		}
		if (!isIncluded) {
			context.getCompensables().add(0, adjacentLogicPosition(candidatePosition));
		}
	}
}