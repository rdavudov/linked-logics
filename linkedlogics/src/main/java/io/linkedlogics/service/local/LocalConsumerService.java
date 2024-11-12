package io.linkedlogics.service.local;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.ConsumerService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.task.ProcessorTask;

public class LocalConsumerService implements ConsumerService {
	
	@Override
	public void consume(Context context) {
		ServiceLocator.getInstance().getProcessorService().process(new ProcessorTask(context));
	}
}