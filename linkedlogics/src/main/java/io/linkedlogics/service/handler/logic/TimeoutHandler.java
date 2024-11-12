package io.linkedlogics.service.handler.logic;

import static io.linkedlogics.context.ContextLog.log;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.Status;
import io.linkedlogics.exception.LogicTimeoutException;
import io.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeoutHandler extends LogicHandler {
	public TimeoutHandler() {

	}
	
	public TimeoutHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(Context logicContext, Object result) {
		Optional<Context> context = ServiceLocator.getInstance().getContextService().get(logicContext.getId());
		
		if (context.isPresent() && context.get().getLogicPosition().startsWith(logicContext.getLogicPosition())) {
			if (context.get().getStatus() != Status.FINISHED && context.get().getStatus() != Status.FAILED) {
				super.handleError(logicContext, new LogicTimeoutException(logicContext.getLogicId()));
			} else {
				ServiceLocator.getInstance().getContextService().remove(context.get().getId());
			}
		}
	}
}
