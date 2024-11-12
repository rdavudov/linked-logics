package io.linkedlogics.redis.service;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.Status;
import io.linkedlogics.redis.repository.ContextRepository;
import io.linkedlogics.service.ContextService;
import io.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisContextService implements ContextService {
	private ContextRepository repository;
	
	public RedisContextService() {
		this.repository = new ContextRepository(new RedisConnectionService().getRedisTemplate());
	}

	@Override
	public Optional<Context> get(String id) {
		try {
			return repository.get(id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<Context> remove(String id) {
		Optional<Context> context = get(id);
		context.ifPresent(c -> {
			try {
				repository.delete(c.getId(), c.getVersion());
			} catch (Exception e) {
				log.error(e.getLocalizedMessage(), e);
			}
		});
		return context;
	}

	@Override
	public void set(Context context) {
		try {
			if (context.getStatus() == Status.INITIAL) {
				repository.create(context);
			} else {
				repository.update(context);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
}
