package io.linkedlogics.jdbc.service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.linkedlogics.jdbc.repository.TopicRepository;
import io.linkedlogics.service.TopicService;

public class JdbcTopicService implements TopicService {
	private String consumerId = UUID.randomUUID().toString();
	private TopicRepository repository;
	private ScheduledExecutorService executor;
	
	@Override
	public void start() {
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				repository.clear(OffsetDateTime.now());
			}
		}, 5, 5, TimeUnit.SECONDS);
	}

	@Override
	public void stop() {
		if (executor != null) { 
			executor.shutdownNow();
		}
	}

	public JdbcTopicService() {
		this.repository = new TopicRepository(new JdbcConnectionService().getDataSource());
	}
	
	public void offer(String topic, String payload) {
		repository.set(topic, payload);
	}
	
	public Optional<String> poll(String topic) {
		return repository.get(topic, consumerId);
	}
}
