package io.linkedlogics.service.handler.logic;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrackingHandler extends LogicHandler {

	public TrackingHandler() {

	}
	
	public TrackingHandler(LogicHandler handler) {
		super(handler);
	}
	
	@Override
	public void handle(Context context, Object result) {
		ServiceLocator.getInstance().getTrackingService().track(context);
		super.handle(context, result);
	}

	@Override
	public void handleError(Context context, Throwable error) {
		super.handleError(context, error);
	}
}
