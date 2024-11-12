package io.linkedlogics.redis.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import redis.embedded.RedisServer;

public class RedisExtension implements BeforeEachCallback, AfterEachCallback {
	private static RedisServer redisServer;
	
	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		if (redisServer != null)
			redisServer.stop();
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		redisServer = new RedisServer(6370);
		redisServer.start();
	}
}