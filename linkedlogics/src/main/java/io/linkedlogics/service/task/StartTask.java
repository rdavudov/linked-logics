package io.linkedlogics.service.task;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.handler.logic.ErrorHandler;
import io.linkedlogics.service.handler.logic.ProcessHandler;
import io.linkedlogics.service.handler.logic.PublishHandler;

public class StartTask extends LinkedLogicsTask {
	public StartTask(Context context) {
		super(context, new ProcessHandler(new PublishHandler()));
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
