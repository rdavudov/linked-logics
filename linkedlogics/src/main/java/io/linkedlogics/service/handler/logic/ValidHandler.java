package io.linkedlogics.service.handler.logic;

import static io.linkedlogics.context.ContextLog.log;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidHandler  extends LogicHandler {

	public ValidHandler() {

	}
	
	public ValidHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(Context context, Object result) {
		Optional<Context> maybeContext = ServiceLocator.getInstance().getContextService().get(context.getId());
		if (maybeContext.isPresent()) {
			Context fullContext = maybeContext.get();
			if (fullContext.getLogicPosition().equals(context.getLogicPosition()) && fullContext.isNotFinished()) {
				super.handle(context, result);
			} else {
				super.handleError(context, new RuntimeException("context not valid"));
			}
		} else {
			super.handleError(context, new RuntimeException("context not found"));
		}
	}
}
