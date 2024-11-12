package io.linkedlogics.service.handler.process;

import static io.linkedlogics.context.ContextLog.log;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.BranchLogicDefinition;
import io.linkedlogics.model.process.LoopLogicDefinition;
import io.linkedlogics.model.process.helper.LogicPositioner;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class LoopFlowHandler extends ProcessFlowHandler {
	public LoopFlowHandler() {

	}

	public LoopFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent()) {
			if (candidate.get() instanceof LoopLogicDefinition) {
				LoopLogicDefinition loopLogic = (LoopLogicDefinition) candidate.get(); 
				
				if (loopLogic.isSatisfied(context)) {
					if (context.getLoopMap().containsKey(candidatePosition)) {
						trace(context, "continue loop", candidatePosition, Flow.RESET);
					} else {
						trace(context, "starting loop", candidatePosition, Flow.RESET);
					}
					int iteration = context.getLoopMap().getOrDefault(candidatePosition, 0) + 1;
					context.getLoopMap().put(candidatePosition, iteration);
					ContextFlow.loop(candidatePosition).name(candidate.get().getName()).result(Boolean.TRUE).message("iteration #" + iteration).log(context);
					return HandlerResult.nextCandidate(candidatePosition + ".1");
				} else {
					context.getLoopMap().remove(candidatePosition);
					trace(context, "closing loop", candidatePosition, Flow.RESET);
					return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
				}
			}
			trace(context, "not a loop", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}