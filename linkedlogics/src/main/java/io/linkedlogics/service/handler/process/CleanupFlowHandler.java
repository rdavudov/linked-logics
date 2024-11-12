package io.linkedlogics.service.handler.process;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.helper.LogicPositioner;

public class CleanupFlowHandler extends ProcessFlowHandler {
	public CleanupFlowHandler() {

	}

	public CleanupFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidatePosition.equals(context.getLogicPosition()) && !candidatePosition.endsWith(LogicPositioner.COMPENSATE)) {
			trace(context, "clean up", candidatePosition, Flow.RESET);
			return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
		} else {
			trace(context, "no clean up", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		}
	}
}
