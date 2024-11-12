package io.linkedlogics.service.task;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.handler.logic.AsyncHandler;
import io.linkedlogics.service.handler.logic.ErrorHandler;
import io.linkedlogics.service.handler.logic.InvokeHandler;
import io.linkedlogics.service.handler.logic.MetricHandler;
import io.linkedlogics.service.handler.logic.OutputHandler;
import io.linkedlogics.service.handler.logic.ProcessHandler;
import io.linkedlogics.service.handler.logic.PublishHandler;
import io.linkedlogics.service.handler.logic.TrackingHandler;
import io.linkedlogics.service.handler.logic.ValidHandler;

public class ProcessorTask extends LinkedLogicsTask {
	public ProcessorTask(Context context) {
		super(context, new ValidHandler(new InvokeHandler(new MetricHandler(new AsyncHandler(new OutputHandler(new ErrorHandler(new ProcessHandler(new TrackingHandler(new PublishHandler())))))))));
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
