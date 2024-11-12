package io.linkedlogics.service.local;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.linkedlogics.LinkedLogicsCallback;
import io.linkedlogics.context.Context;
import io.linkedlogics.service.CallbackService;
import io.linkedlogics.service.config.ServiceConfiguration;
import io.linkedlogics.service.local.config.LocalCallbackServiceConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalCallbackService implements CallbackService {
	private ConcurrentHashMap<String, LinkedLogicsCallback> callbackMap;
	private ScheduledExecutorService scheduler;
	private LocalCallbackServiceConfig config = new ServiceConfiguration().getConfig(LocalCallbackServiceConfig.class);
	
	@Override
	public void start() {
		callbackMap = new ConcurrentHashMap<>();
		scheduler = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	public void stop() {
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
	}

	@Override
	public void set(String contextId, LinkedLogicsCallback callback) {
		callbackMap.put(contextId, callback);
		scheduler.schedule(() -> Optional.ofNullable(callbackMap.remove(contextId)).ifPresent((c) -> c.onTimeout()), config.getExpireTimeOrDefault(5), TimeUnit.SECONDS);
	}

	@Override
	public void publish(Context context) {
		Optional.ofNullable(callbackMap.remove(context.getId())).ifPresent((c) -> callback(context, c));
	}
}
