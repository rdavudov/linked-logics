package io.linkedlogics.service.handler.process;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.SingleLogicDefinition;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class SuccessFlowHandler extends ProcessFlowHandler {
	public SuccessFlowHandler() {

	}

	public SuccessFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidatePosition.equals(context.getLogicPosition())) {
			if (context.getError() == null && candidate.get() instanceof SingleLogicDefinition && ((SingleLogicDefinition) candidate.get()).getCompensationLogic() != null) {
				trace(context, "adding compensation logic to compensables", candidatePosition, Flow.CONTINUE);
				context.getCompensables().add(((SingleLogicDefinition)candidate.get()).getCompensationLogic().getPosition());
			} else {
				trace(context, "nothing to compensate", candidatePosition, Flow.CONTINUE);
			}
		}
		
		return super.handle(candidate, candidatePosition, context);
	}
}
