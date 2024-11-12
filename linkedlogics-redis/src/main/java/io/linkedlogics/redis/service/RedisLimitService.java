package io.linkedlogics.redis.service;

import java.time.OffsetDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.linkedlogics.redis.repository.ContextRepository;
import io.linkedlogics.redis.repository.LimitRepository;
import io.linkedlogics.service.LimitService;
import io.linkedlogics.service.config.ServiceConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisLimitService implements LimitService {
	private LimitRepository repository;
	
	public RedisLimitService() {
		repository = new LimitRepository(new RedisConnectionService().getRedisTemplate());
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
			return repository.create(getKey(key, timestamp, interval), timestamp, limit, increment);
		} else {
			return repository.update(getKey(key, timestamp, interval), timestamp, limit, increment);
		}
	}
}
