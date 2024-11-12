package io.linkedlogics.service.task;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.handler.logic.CancelHandler;
import io.linkedlogics.service.handler.logic.ErrorHandler;

public class CancelTask extends LinkedLogicsTask {
	public CancelTask(Context context) {
		super(context, new CancelHandler());
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
