package io.linkedlogics.service.local;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.linkedlogics.service.ProcessorService;
import io.linkedlogics.service.config.ServiceConfiguration;
import io.linkedlogics.service.local.config.LocalProcessorServiceConfig;
import io.linkedlogics.service.task.LinkedLogicsTask;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalProcessorService implements ProcessorService {
	private static final int DEFAULT_THREADS = 2;
	
	private ThreadPoolExecutor service;
	private ScheduledExecutorService taskManager;
	private LocalProcessorServiceConfig config = new ServiceConfiguration().getConfig(LocalProcessorServiceConfig.class);
	
	@Override
	public void start() {
		service = new ThreadPoolExecutor(config.getThreads(DEFAULT_THREADS), config.getThreads(DEFAULT_THREADS), 
				0L, TimeUnit.MILLISECONDS, new CompleteBlockingQueue<>(config.getCapacity(config.getThreads(DEFAULT_THREADS))));
		taskManager = Executors.newSingleThreadScheduledExecutor();
	}
	
	@Override
	public void stop() {
		if (service != null) {
			service.shutdownNow();
		}
	}

	@Override
	@SneakyThrows
	public void process(LinkedLogicsTask task) {
		task.setService(this);
		Future<?> future = service.submit(task);
		taskManager.schedule(() -> {
			if (!future.isDone()) {
				future.cancel(true);
			}
		}, config.getMaxBlocking(60), TimeUnit.SECONDS);
	}

	private static class CompleteBlockingQueue<T> extends ArrayBlockingQueue<T> {
		private static final long serialVersionUID = -817911632652898426L;
		
		public CompleteBlockingQueue(int capacity) {
			super(capacity);
		}

		@Override
		public boolean offer(T e) {
			try {
				super.put(e);
				return true;
			} catch (InterruptedException ex) {
				return false;
			}
		}

		@Override
		public boolean offer(T e, long timeout, TimeUnit unit) throws InterruptedException {
			try {
				super.put(e);
				return true;
			} catch (InterruptedException ex) {
				return false;
			}
		}
	}
}
