package io.linkedlogics.service.handler.process;

import java.time.OffsetDateTime;
import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextError;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.context.Status;
import io.linkedlogics.context.ContextError.ErrorType;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.RetryLogicDefinition;
import io.linkedlogics.service.SchedulerService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.SchedulerService.Schedule;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class RetryFlowHandler extends ProcessFlowHandler {
	public RetryFlowHandler() {

	}
	
	public RetryFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidate.get().getRetry() != null) {
			RetryLogicDefinition retry = candidate.get().getRetry();
			
			if (context.getError() != null && matches(context.getError(), retry)) {
				if (context.getError().getType() == ErrorType.TEMPORARY) {
					int retries = context.getRetries().getOrDefault(candidatePosition, 0);
					if (retries < retry.getMaxRetries()) {
						context.getRetries().put(candidatePosition, retries + 1);
						context.setError(null);
						
						if (candidate.get().getRetry().getSeconds() > 0) {
							context.setStatus(Status.SCHEDULED);
							OffsetDateTime scheduledAt = OffsetDateTime.now().plusSeconds(candidate.get().getRetry().getSeconds());
							Schedule schedule = new Schedule(context.getId(), null, candidatePosition, scheduledAt, SchedulerService.ScheduleType.RETRY); 
							ServiceLocator.getInstance().getSchedulerService().schedule(schedule);
							ContextFlow.retry(candidatePosition).name(candidate.get().getName()).result(Boolean.TRUE).message("retrying #" + retries).log(context);
							trace(context, "retry is scheduled " + schedule.getExpiresAt(), candidatePosition, Flow.TERMINATE);
							return HandlerResult.noCandidate();
						} else {
							ContextFlow.retry(candidatePosition).name(candidate.get().getName()).result(Boolean.TRUE).message("retrying now #" + retries).log(context);
							trace(context, "retrying immediately", candidatePosition, Flow.RESET);
							return HandlerResult.selectCandidate(candidate);
						}
					} else {
						trace(context, "no retry max retry reached", candidatePosition, Flow.CONTINUE);
						ContextFlow.retry(candidatePosition).name(candidate.get().getName()).result(Boolean.FALSE).message("no retry #" + retries).log(context);
						context.getRetries().remove(candidatePosition);
					}
				} else {
					trace(context, "no retry permanent error", candidatePosition, Flow.CONTINUE);
				}
			} else {
				trace(context, "no error no retry", candidatePosition, Flow.CONTINUE);
				context.getRetries().remove(candidatePosition);
			}
		} else {
			trace(context, "no retry", candidatePosition, Flow.CONTINUE);
		}
		
		return super.handle(candidate, candidatePosition, context);
	}
	
	private boolean matches(ContextError error, RetryLogicDefinition retryDefinition) {
		boolean matchResult = false;
		
		if ((retryDefinition.getErrorCodeSet() == null || retryDefinition.getErrorCodeSet().isEmpty()) 
				&& (retryDefinition.getErrorMessageSet() == null || retryDefinition.getErrorMessageSet().isEmpty())) {
			matchResult = true;
		} else if (retryDefinition.getErrorCodeSet() != null && retryDefinition.getErrorCodeSet().contains(error.getCode())) {
			matchResult = true;
		} else if (error.getMessage() != null 
				&& retryDefinition.getErrorMessageSet() != null
					&& retryDefinition.getErrorMessageSet().stream().filter(s -> error.getMessage().contains(s)).findAny().isPresent()) { 
			matchResult = true;
		}

		return retryDefinition.isExclude() ? !matchResult : matchResult;
	}
}
