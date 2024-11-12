package io.linkedlogics.service.task;

import io.linkedlogics.context.Context;
import io.linkedlogics.exception.AsyncTimeoutException;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.handler.logic.ErrorHandler;
import io.linkedlogics.service.handler.logic.ProcessHandler;
import io.linkedlogics.service.handler.logic.PublishHandler;
import io.linkedlogics.service.handler.logic.TrackingHandler;
import io.linkedlogics.service.handler.logic.ValidHandler;

public class AsyncCallbackExpireTask extends LinkedLogicsTask {
	
	public AsyncCallbackExpireTask(Context context) {
		super(context, new ValidHandler(new ErrorHandler(new ProcessHandler(new TrackingHandler(new PublishHandler())))));
	}
	
	@Override
	public void handle() {
		try {
			ServiceLocator.getInstance().getAsyncService().remove(context.getId()).ifPresent(c -> {
				handler.handleError(c, new AsyncTimeoutException(context.getLogicId(), context.getLogicVersion()));
			});
		} catch (Throwable e) {
			new ErrorHandler(new ProcessHandler()).handleError(context, e);
		}
	}
}
