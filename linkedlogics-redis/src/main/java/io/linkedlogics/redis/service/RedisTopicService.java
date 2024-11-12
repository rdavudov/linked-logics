package io.linkedlogics.redis.service;

import java.util.Optional;

import io.linkedlogics.service.TopicService;

public class RedisTopicService implements TopicService {

	@Override
	public void offer(String arg0, String arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<String> poll(String arg0) {
		throw new UnsupportedOperationException();
	}
}
