package io.linkedlogics.jdbc.service;

import java.util.List;

import io.linkedlogics.service.TriggerService;
import io.linkedlogics.jdbc.repository.TriggerRepository;

public class JdbcTriggerService implements TriggerService {
	private TriggerRepository repository;
	
	public JdbcTriggerService() {
		repository = new TriggerRepository(new JdbcConnectionService().getDataSource());
	}
	
	@Override
	public List<Trigger> get(String context) {
		return repository.get(context);
	}

	@Override
	public void set(String context, Trigger trigger) {
		repository.create(context, trigger);
	}
}
