package io.linkedlogics.service.common;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.linkedlogics.service.MapperService;
import io.linkedlogics.service.QueueService;
import io.linkedlogics.service.SchedulerService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.config.ServiceConfiguration;
import io.linkedlogics.service.local.config.LocalQueueServiceConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueueSchedulerService implements SchedulerService, Runnable {
	private static final String QUEUE_SEC = "scheduler_sec_";
	private static final String QUEUE_MIN = "scheduler_min_";
	
	private ScheduledExecutorService scheduler;
	private Thread consumer;
	private ArrayBlockingQueue<String> queueQueue;
	private boolean isRunning;
	private LocalQueueServiceConfig config = new ServiceConfiguration().getConfig(LocalQueueServiceConfig.class);
	
	@Override
	public void start() {
		queueQueue = new ArrayBlockingQueue<String>(config.getQueueSize(10000));
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				queueQueue.offer(QUEUE_SEC + OffsetDateTime.now().getSecond());
			}
		}, 0, 1, TimeUnit.SECONDS);
		
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				queueQueue.offer(QUEUE_MIN + OffsetDateTime.now().getMinute());
				
			}
		}, (60 - OffsetDateTime.now().getSecond()), 60, TimeUnit.SECONDS);
		
		consumer = new Thread(this);
		consumer.start();
	}

	@Override
	public void stop() {
		Optional.ofNullable(scheduler).ifPresent(s -> s.shutdownNow());
		isRunning = false;
		Optional.ofNullable(consumer).ifPresent(c -> c.interrupt());
	}

	public void run() {
		isRunning = true;
		while (isRunning) {
			String queue = queueQueue.poll();
			if (queue != null) {
				QueueService queueService = ServiceLocator.getInstance().getService(QueueService.class);
				MapperService mapperService = ServiceLocator.getInstance().getMapperService();
				Optional<String> scheduleMessage = queueService.poll(queue);

				while (scheduleMessage.isPresent()) {
					try {
						Schedule schedule = mapperService.getMapper().readValue(scheduleMessage.get(), Schedule.class);
						if (schedule.getExpiresAt().withNano(0).isBefore(OffsetDateTime.now())) {
							handle(schedule);
						} else {
							schedule(schedule);
						}
					} catch (JsonProcessingException e) {
						log.error(e.getLocalizedMessage(), e);
					}
					scheduleMessage = queueService.poll(queue);
				}
			} else {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) { }
			}
		}
	}

	@Override
	public void schedule(Schedule schedule) {
		QueueService queueService = ServiceLocator.getInstance().getService(QueueService.class);
		MapperService mapperService = ServiceLocator.getInstance().getMapperService();
		
		OffsetDateTime now = OffsetDateTime.now();
		
		try {
			if (now.getMinute() == schedule.getExpiresAt().getMinute()) {
				queueService.offer(QUEUE_SEC + schedule.getExpiresAt().getSecond(), mapperService.getMapper().writeValueAsString(schedule));
			} else {
				queueService.offer(QUEUE_MIN + schedule.getExpiresAt().getMinute(), mapperService.getMapper().writeValueAsString(schedule));
			}
		} catch (JsonProcessingException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
}
