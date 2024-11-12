package io.linkedlogics.service.handler.process;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class ForcedFlowHandler extends ProcessFlowHandler {
	public ForcedFlowHandler() {

	}

	public ForcedFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && context.getError() != null) {
			if (candidate.get().getForced() != null && candidate.get().getForced()) {
				trace(context, "logic is forced", candidatePosition, Flow.CONTINUE);
				return super.handle(candidate, candidatePosition, context);
			} else {
				trace(context, "logic is not forced", candidatePosition, Flow.RESET);
				return HandlerResult.nextCandidate(candidatePosition);
			}
		} else {
			trace(context, "no error no forcing", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		}
	}
}