package io.linkedlogics.service.handler.process;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextError;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.context.ContextError.ErrorType;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.VerifyLogicDefinition;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class VerifyFlowHandler extends ProcessFlowHandler {
	public VerifyFlowHandler() {

	}

	public VerifyFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent()) {
			if (candidate.get() instanceof VerifyLogicDefinition) {
				VerifyLogicDefinition verifyLogic = (VerifyLogicDefinition) candidate.get(); 
				if (!verifyLogic.isVerified(context)) {
					context.setError(new ContextError(verifyLogic.getErrorCode(), verifyLogic.getErrorMessage(), ErrorType.PERMANENT));
					ContextFlow.verify(candidatePosition).name(candidate.get().getName()).result(Boolean.FALSE).message("verification failed").log(context);
					trace(context, "not verified", candidatePosition, Flow.RESET);
					return HandlerResult.nextCandidate(candidatePosition);
				} else {
					ContextFlow.verify(candidatePosition).name(candidate.get().getName()).result(Boolean.TRUE).message("verification success").log(context);
					trace(context, "verified", candidatePosition, Flow.RESET);
					return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
				}
			} else {
				trace(context, "no verify", candidatePosition, Flow.CONTINUE);
			}

			return super.handle(candidate, candidatePosition, context);
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}