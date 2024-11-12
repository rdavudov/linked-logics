package io.linkedlogics.service.task;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.handler.logic.ErrorHandler;
import io.linkedlogics.service.handler.logic.MetricHandler;
import io.linkedlogics.service.handler.logic.ProcessHandler;
import io.linkedlogics.service.handler.logic.PublishHandler;
import io.linkedlogics.service.handler.logic.TimeoutHandler;
import io.linkedlogics.service.handler.logic.TrackingHandler;

public class TimeoutTask extends LinkedLogicsTask {
	public TimeoutTask(Context context) {
		super(context, new TimeoutHandler(new MetricHandler(new ErrorHandler(new ProcessHandler(new TrackingHandler(new PublishHandler()))))));
	}
	
	@Override
	public void handle() {
		try {
			handler.handle(context, null);
		} catch (Throwable e) {
			new ErrorHandler().handleError(context, e);
		}
	}
}
