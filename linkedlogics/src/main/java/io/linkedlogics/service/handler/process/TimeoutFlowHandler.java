package io.linkedlogics.service.handler.process;

import java.time.OffsetDateTime;
import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class TimeoutFlowHandler extends ProcessFlowHandler {
	public TimeoutFlowHandler() {

	}
	
	public TimeoutFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && 
				candidate.get().getTimeout() != null && 
					candidate.get().getTimeout().getSeconds() > 0) {
			OffsetDateTime expiresAt = OffsetDateTime.now().plusSeconds(candidate.get().getTimeout().getSeconds());
			context.setExpiresAt(expiresAt);
			trace(context, "timeout at " + expiresAt, candidatePosition, Flow.CONTINUE);
		} else {
			trace(context, "no timeout", candidatePosition, Flow.CONTINUE);
		}
		
		return super.handle(candidate, candidatePosition, context);
	}
}
