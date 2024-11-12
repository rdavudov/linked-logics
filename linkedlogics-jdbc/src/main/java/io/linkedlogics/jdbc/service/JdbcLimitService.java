package io.linkedlogics.jdbc.service;

import java.time.OffsetDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.linkedlogics.jdbc.repository.LimitRepository;
import io.linkedlogics.jdbc.service.config.JdbcLimitServiceConfig;
import io.linkedlogics.service.LimitService;
import io.linkedlogics.service.config.ServiceConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdbcLimitService implements LimitService {
	private LimitRepository repository;
	private ScheduledExecutorService scheduler;
	private JdbcLimitServiceConfig config = new ServiceConfiguration().getConfig(JdbcLimitServiceConfig.class);
	
	@Override
	public void start() {
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(() -> {
			repository.deleteExpired(OffsetDateTime.now());
		}, config.getCleanupInterval(), config.getCleanupInterval(), TimeUnit.SECONDS);
	}
	
	@Override
	public void stop() {
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
	}
	
	public JdbcLimitService() {
		repository = new LimitRepository(new JdbcConnectionService().getDataSource());
	}
	
	@Override
	public void reset(String key, OffsetDateTime timestamp, Interval interval) {
		repository.delete(getKey(key, timestamp, interval));
	}

	@Override
	public boolean check(String key, OffsetDateTime timestamp, Interval interval, long limit) {
		return repository.get(getKey(key, timestamp, interval)).map(c -> c < limit).orElse(true);
	}

	@Override
	public boolean increment(String key, OffsetDateTime timestamp, Interval interval, long limit, long increment) {
		if (repository.get(getKey(key, timestamp, interval)).isEmpty()) {
			return repository.create(getKey(key, timestamp, interval), timestamp, limit, increment) > 0;
		} else {
			return repository.update(getKey(key, timestamp, interval), timestamp, limit, increment) > 0;
		}
	}
}
