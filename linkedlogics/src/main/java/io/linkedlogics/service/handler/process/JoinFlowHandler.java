package io.linkedlogics.service.handler.process;

import java.util.Arrays;
import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.context.Status;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.TriggerService;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class JoinFlowHandler extends ProcessFlowHandler {
	public JoinFlowHandler() {

	}

	public JoinFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidate.get().getJoin() != null) {
			Optional<String> missingContextId = Arrays.stream(candidate.get().getJoin().getKey())
					.map(k -> context.getJoinMap().get(k))
					.map(k -> ServiceLocator.getInstance().getContextService().get(k))
					.filter(c -> c.get().getStatus() == Status.STARTED || c.get().getStatus() == Status.INITIAL)
					.map(c -> c.get().getId())
					.findAny();
			
			if (missingContextId.isPresent()) {
				ServiceLocator.getInstance().getTriggerService().set(missingContextId.get(), new TriggerService.Trigger(context.getId(), candidatePosition));
				context.setStatus(Status.WAITING);
				trace(context, "missing finished context " + missingContextId.get(), candidatePosition, Flow.TERMINATE);
				return HandlerResult.noCandidate();
			} else {
				context.setStatus(Status.STARTED);
				trace(context, "join successful", candidatePosition, Flow.CONTINUE);
				ContextFlow.join(candidatePosition).name(candidate.get().getName()).result(Boolean.TRUE).message("join successful").log(context);
				return super.handle(candidate, candidatePosition, context);
			}
		} else {
			trace(context, "no join", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		}
	}
}
