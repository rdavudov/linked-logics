package io.linkedlogics.service.handler.process;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.GroupLogicDefinition;
import io.linkedlogics.model.process.ProcessLogicDefinition;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class GroupFlowHandler extends ProcessFlowHandler {
	public GroupFlowHandler() {

	}

	public GroupFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && (candidate.get() instanceof GroupLogicDefinition || candidate.get() instanceof ProcessLogicDefinition)) {
			trace(context, "executing group", candidatePosition, Flow.RESET);
			ContextFlow.group(candidatePosition).name(candidate.get().getName()).result(Boolean.TRUE).message("executing group").log(context);
			return HandlerResult.nextCandidate(candidatePosition + ".1");
		} else {
			trace(context, "no group", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		}
	}
}