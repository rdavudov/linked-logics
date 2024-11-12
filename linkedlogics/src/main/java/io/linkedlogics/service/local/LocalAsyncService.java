package io.linkedlogics.service.local;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.AsyncService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.config.ServiceConfiguration;
import io.linkedlogics.service.local.config.LocalAsyncServiceConfig;
import io.linkedlogics.service.task.AsyncCallbackErrorTask;
import io.linkedlogics.service.task.AsyncCallbackExpireTask;
import io.linkedlogics.service.task.AsyncCallbackTask;

public class LocalAsyncService implements AsyncService {
	private ConcurrentHashMap<String, Context> contextMap;
	private ScheduledExecutorService scheduler;
	private ThreadLocal<String> contextId;
	private LocalAsyncServiceConfig config = new ServiceConfiguration().getConfig(LocalAsyncServiceConfig.class);
	
	@Override
	public void start() {
		contextMap = new ConcurrentHashMap<>();
		contextId = new ThreadLocal<>();
		scheduler = Executors.newSingleThreadScheduledExecutor();
	}
	
	@Override
	public void stop() {
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
	}
	
	@Override
	public void set(Context context) {
		contextMap.put(context.getId(), context);
		scheduler.schedule(new AsyncCallbackExpireTask(context), config.getExpireTimeOrDefault(5), TimeUnit.SECONDS);
	}

	@Override
	public Optional<Context> remove(String contextId) {
		return Optional.ofNullable(contextMap.remove(contextId));
	}

	@Override
	public void asyncCallback(String contextId, Object result) {
		if (contextMap.containsKey(contextId)) {
			remove(contextId).ifPresent(c -> {
				ServiceLocator.getInstance().getProcessorService().process(new AsyncCallbackTask(c, result));
			});
		}
	}

	@Override
	public void asyncCallerror(String contextId, Throwable error) {
		if (contextMap.containsKey(contextId)) {
			remove(contextId).ifPresent(c -> {
				ServiceLocator.getInstance().getProcessorService().process(new AsyncCallbackErrorTask(c, error));
			});
		}
	}

	@Override
	public void setContextId(String contextId) {
		this.contextId.set(contextId);
	}
	
	@Override
	public void unsetContextId() {
		this.contextId.remove();
	}

	@Override
	public String getContextId() {
		return this.contextId.get();
	}
}
