package io.linkedlogics.service.task;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.handler.logic.ErrorHandler;
import io.linkedlogics.service.handler.logic.ProcessHandler;
import io.linkedlogics.service.handler.logic.PublishHandler;
import io.linkedlogics.service.handler.logic.TrackingHandler;
import io.linkedlogics.service.handler.logic.ValidHandler;

public class RetryTask extends LinkedLogicsTask {
	public RetryTask(Context context) {
		super(context, new ValidHandler(new ProcessHandler(new TrackingHandler(new PublishHandler()))));
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
