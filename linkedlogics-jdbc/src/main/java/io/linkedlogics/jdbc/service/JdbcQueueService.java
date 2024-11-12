package io.linkedlogics.jdbc.service;

import java.util.Optional;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.service.QueueService;
import io.linkedlogics.jdbc.repository.QueueRepository;

public class JdbcQueueService implements QueueService {
	private QueueRepository repository;
	
	public JdbcQueueService() {
		this.repository = new QueueRepository(new JdbcConnectionService().getDataSource());
	}
	
	public void offer(String queue, String payload) {
		repository.set(queue, payload);
	}
	
	public Optional<String> poll(String queue) {
		return repository.get(queue, LinkedLogics.getApplicationName());
	}
}
