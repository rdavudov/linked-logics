package io.linkedlogics.service.task;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.handler.logic.ErrorHandler;
import io.linkedlogics.service.handler.logic.OutputHandler;
import io.linkedlogics.service.handler.logic.ProcessHandler;
import io.linkedlogics.service.handler.logic.PublishHandler;
import io.linkedlogics.service.handler.logic.TrackingHandler;
import io.linkedlogics.service.handler.logic.ValidHandler;
import lombok.Getter;

@Getter
public class AsyncCallbackTask extends LinkedLogicsTask {
	private Object result;
	
	public AsyncCallbackTask(Context context, Object result) {
		super(context, new ValidHandler(new OutputHandler(new ErrorHandler(new ProcessHandler(new TrackingHandler(new PublishHandler()))))));
		this.result = result;
	}

	@Override
	public void handle() {
		try {
			handler.handle(context, result);
		} catch (Throwable e) {
			new ErrorHandler(new ProcessHandler()).handleError(context, e);
		}
	}
}
