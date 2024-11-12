package io.linkedlogics.service.handler.process;

import java.time.OffsetDateTime;
import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.context.Status;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.service.SchedulerService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.SchedulerService.Schedule;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class DelayFlowHandler extends ProcessFlowHandler {
	public DelayFlowHandler() {

	}
	
	public DelayFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidate.get().getDelay() != null && candidate.get().getDelay().getSeconds() > 0) {
			if (context.getStatus() == Status.SCHEDULED) {
				context.setStatus(Status.STARTED);
				trace(context, "delaying finished", candidatePosition, Flow.CONTINUE);
				return super.handle(candidate, candidatePosition, context);
			} else {
				context.setStatus(Status.SCHEDULED);
				OffsetDateTime scheduledAt = OffsetDateTime.now().plusSeconds(candidate.get().getDelay().getSeconds());
				Schedule schedule = new Schedule(context.getId(), null, candidatePosition, scheduledAt, SchedulerService.ScheduleType.DELAY); 
				ContextFlow.delay(candidatePosition).name(candidate.get().getName()).result(Boolean.TRUE).message("delaying for " + candidate.get().getDelay().getSeconds() + " secs").log(context);
				trace(context, "delaying execution till " + schedule.getExpiresAt(), candidatePosition, Flow.TERMINATE);
				ServiceLocator.getInstance().getSchedulerService().schedule(schedule);
				return HandlerResult.noCandidate();
			}
		} else {
			trace(context, "no delay", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		}
	}
}
