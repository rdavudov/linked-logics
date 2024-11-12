package io.linkedlogics.redis.service;

import java.util.List;

import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.TriggerService;
import io.linkedlogics.redis.repository.TriggerRepository;

public class RedisTriggerService implements TriggerService {
	private TriggerRepository repository;
	
	public RedisTriggerService() {
		this.repository = new TriggerRepository(new RedisConnectionService().getRedisTemplate());
	}
	
	@Override
	public List<Trigger> get(String id) {
		return repository.get(id);
	}

	@Override
	public void set(String id, Trigger trigger) {
		repository.create(id, trigger);
	}
}
