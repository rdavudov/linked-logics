package io.linkedlogics.service.task;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.handler.logic.ErrorHandler;
import io.linkedlogics.service.handler.logic.ProcessHandler;
import io.linkedlogics.service.handler.logic.PublishHandler;
import io.linkedlogics.service.handler.logic.TrackingHandler;
import io.linkedlogics.service.handler.logic.ValidHandler;
import lombok.Getter;

@Getter
public class AsyncCallbackErrorTask extends LinkedLogicsTask {
	private Throwable error;
	
	public AsyncCallbackErrorTask(Context context, Throwable error) {
		super(context, new ValidHandler(new ErrorHandler(new ProcessHandler(new TrackingHandler(new PublishHandler())))));
		this.error = error;
	}

	@Override
	public void handle() {
		try {
			handler.handleError(context, error);
		} catch (Throwable e) {
			new ErrorHandler(new ProcessHandler()).handleError(context, e);
		}
	}
}
