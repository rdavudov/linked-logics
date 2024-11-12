package io.linkedlogics.redis.service;

import java.util.Optional;

import io.linkedlogics.service.QueueService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.redis.repository.QueueRepository;

public class RedisQueueService implements QueueService {
	private QueueRepository repository;
	
	public RedisQueueService() {
		this.repository = new QueueRepository(new RedisConnectionService().getRedisTemplate());
	}
	
	public void offer(String queue, String payload) {
		repository.offer(queue, payload);
	}
	
	public Optional<String> poll(String queue) {
		return repository.poll(queue);
	}
}
