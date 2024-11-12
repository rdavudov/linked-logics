package io.linkedlogics.redis.repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.ServiceLocator;

public class ContextRepository extends JedisRepository {
	private static final String CONTEXT = "context:";
	
	public ContextRepository(StringRedisTemplate redisTemplate) {
		super(redisTemplate);
	}
	
	public void create(Context context) throws Exception {
		redisTemplate.opsForValue().set(getKey(context), getValue(context), 120, TimeUnit.SECONDS);
	}

	public void update(Context context) throws Exception {
		String currentValue = redisTemplate.opsForValue().get(getKey(context));
		if (currentValue != null && !currentValue.isEmpty()) {
			if (getValue(currentValue).getVersion() <= context.getVersion()) {
				redisTemplate.opsForValue().set(getKey(context), getValue(context), 120, TimeUnit.SECONDS);
			}
		} else {
			redisTemplate.opsForValue().set(getKey(context), getValue(context), 120, TimeUnit.SECONDS);
		}
	}

	public Optional<Context> get(String id) throws Exception {
		String currentValue = redisTemplate.opsForValue().get(getKey(id));
		if (currentValue != null && !currentValue.isEmpty()) {
			return Optional.of(getValue(currentValue));
		} 
		return Optional.empty();
	}

	public void delete(String id, int version) throws Exception {
		String currentValue = redisTemplate.opsForValue().get(getKey(id));
		if (currentValue != null && !currentValue.isEmpty()) {
			if (getValue(currentValue).getVersion() <= version) {
				redisTemplate.delete(getKey(id));
			}
		}
	}
	
	private String getKey(Context context) {
		return CONTEXT + context.getId();
	}
	
	private String getKey(String id) {
		return CONTEXT + id;
	}
	
	private String getValue(Context context) throws Exception {
		return ServiceLocator.getInstance().getMapperService().getMapper().writeValueAsString(context);
	}
	
	private Context getValue(String context) throws Exception {
		return ServiceLocator.getInstance().getMapperService().getMapper().readValue(context, Context.class);
	}
}
