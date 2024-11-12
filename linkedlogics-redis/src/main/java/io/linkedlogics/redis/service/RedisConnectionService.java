package io.linkedlogics.redis.service;

import java.time.Duration;

import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import io.linkedlogics.redis.service.config.RedisConnectionServiceConfig;
import io.linkedlogics.service.LinkedLogicsService;
import io.linkedlogics.service.config.ServiceConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisConnectionService implements LinkedLogicsService {
	private static StringRedisTemplate redisTemplate;
	
	private RedisConnectionServiceConfig config = new ServiceConfiguration().getConfig(RedisConnectionServiceConfig.class);
	
	public StringRedisTemplate getRedisTemplate() {
		if (redisTemplate != null) {
			return redisTemplate;
		} else {
			synchronized (RedisConnectionService.class) {
				if (redisTemplate != null) {
					return redisTemplate;
				} else {
					redisTemplate = initRedisTemplate();
					return redisTemplate;
				}
			}
		}
	}
	
	private StringRedisTemplate initRedisTemplate() {
		log.info("connecting to redis@{}:{}", config.getHost(), config.getPort());
		
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(config.getHost());
		redisStandaloneConfiguration.setPort(config.getPort());
		config.getPassword().ifPresent(redisStandaloneConfiguration::setPassword);

		JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
		config.getTimeout().map(t -> Duration.ofMillis(t)).ifPresent(jedisClientConfiguration::connectTimeout);

		jedisClientConfiguration.usePooling();
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
		jedisConnectionFactory.afterPropertiesSet();
		
		StringRedisTemplate redisTemplate = new StringRedisTemplate();
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		redisTemplate.afterPropertiesSet();
		
		return redisTemplate;
	}
}
