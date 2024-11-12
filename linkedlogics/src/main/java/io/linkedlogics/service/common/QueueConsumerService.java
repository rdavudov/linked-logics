package io.linkedlogics.service.common;

import java.time.OffsetDateTime;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.context.Context;
import io.linkedlogics.service.ConsumerService;
import io.linkedlogics.service.LimitService;
import io.linkedlogics.service.LimitService.Interval;
import io.linkedlogics.service.QueueService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.common.config.QueueConsumerServiceConfig;
import io.linkedlogics.service.config.ServiceConfiguration;
import io.linkedlogics.service.task.ProcessorTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueueConsumerService implements ConsumerService, Runnable {
	private Thread consumer;
	private boolean isRunning;
	private QueueConsumerServiceConfig config = new ServiceConfiguration().getConfig(QueueConsumerServiceConfig.class);
	private String limitKey = "CONSUMER:" + LinkedLogics.getApplicationName();
	
	@Override
	public void start() {
		consumer = new Thread(this);
		consumer.start();
	}

	@Override
	public void stop() {
		isRunning = false;
		if (consumer != null) {
			consumer.interrupt();
		}
	}

	@Override
	public void run() {
		isRunning = true;
		
		while (isRunning) {
			try {
				QueueService queueService = ServiceLocator.getInstance().getQueueService();
				Optional<String> message = queueService.poll(LinkedLogics.getApplicationName());
				if (message.isPresent()) {
					ObjectMapper mapper = ServiceLocator.getInstance().getMapperService().getMapper();
					try {
						consume(mapper.readValue(message.get(), Context.class));
					} catch (Exception e) {
						log.error(e.getLocalizedMessage(), e);
					}
					
					if (config.getRateLimit().isPresent()) {
						LimitService limitService = ServiceLocator.getInstance().getLimitService();
						Interval interval = Enum.valueOf(Interval.class, config.getRateInterval(Interval.SECOND.name()));
						boolean limitCheckResult = limitService.increment(limitKey, OffsetDateTime.now(), interval, config.getRateLimit().get(), 1);
						if (!limitCheckResult) {
							Thread.sleep(limitService.getTimeToWait(OffsetDateTime.now(), interval));
						}
					}
				} else {
					Thread.sleep(config.getDelay(1));
				}
			} catch (InterruptedException e) {}
		}
	}

	@Override
	public void consume(Context context) {
		ServiceLocator.getInstance().getProcessorService().process(new ProcessorTask(context));
	}
}