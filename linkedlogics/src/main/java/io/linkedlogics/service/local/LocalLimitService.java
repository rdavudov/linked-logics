package io.linkedlogics.service.local;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.linkedlogics.service.LimitService;

public class LocalLimitService implements LimitService {
	private ConcurrentHashMap<String, AtomicLong> limits = new ConcurrentHashMap<>();
	private ScheduledExecutorService scheduler;
	
	@Override
	public void start() {
		scheduler = Executors.newSingleThreadScheduledExecutor();
	}
	
	@Override
	public void stop() {
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
	}
	
	public void remove(String key) {
		limits.get(key);
	}
	
	@Override
	public void reset(String key, OffsetDateTime timestamp, Interval interval) {
		Optional.ofNullable(limits.get(getKey(key, timestamp, interval))).ifPresent(l -> l.set(0));
	}

	@Override
	public boolean check(String key, OffsetDateTime timestamp, Interval interval, long limit) {
		Optional<AtomicLong> counter = Optional.ofNullable(limits.get(getKey(key, timestamp, interval)));
		
		if (counter.isPresent() && counter.get().get() >= limit) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean increment(String key, OffsetDateTime timestamp, Interval interval, long limit, long increment) {
		String counterKey = getKey(key, timestamp, interval);
		
		Optional<AtomicLong> counter = Optional.ofNullable(limits.get(counterKey));
		
		if (counter.isEmpty()) {
			synchronized (LocalLimitService.class) {
				counter = Optional.ofNullable(limits.get(counterKey));
				if (counter.isEmpty()) {
					AtomicLong newCounter = new AtomicLong(0);
					limits.put(getKey(key, timestamp, interval), newCounter);
					counter = Optional.of(newCounter);
					scheduler.schedule(() -> remove(counterKey), getSecondsByInterval(interval) + 1, TimeUnit.SECONDS);
				}
			}
		}
		
		long lastCount = counter.get().addAndGet(increment);
		return lastCount <= limit;
	}
	
	private long getSecondsByInterval(Interval interval) {
		if (interval == Interval.SECOND) {
			return 1;
		} else if (interval == Interval.MINUTE) {
			return 60;
		} else if (interval == Interval.HOUR) {
			return 3600;
		} else if (interval == Interval.DAY) {
			return 86400;
		} else {
			throw new IllegalArgumentException(interval + " unsupported interval");
		}
	}
}
