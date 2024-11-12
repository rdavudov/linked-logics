package io.linkedlogics.service.handler.logic;

import static io.linkedlogics.context.ContextLog.log;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextError;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorHandler extends LogicHandler {
	
	public ErrorHandler() {

	}
	
	public ErrorHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(Context context, Object result) {
		super.handle(context, result);
	}

	@Override
	public void handleError(Context context, Throwable error) {
		context.setError(ContextError.of(error));
		log(context).handler(this).error(error).error();
		super.handleError(context, error);
	}
}
