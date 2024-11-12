package io.linkedlogics.redis.service;

import io.linkedlogics.service.ServiceConfigurer;
import io.linkedlogics.service.common.QueueSchedulerService;

public class RedisServiceConfigurer extends ServiceConfigurer {
	public RedisServiceConfigurer() {
		configure(new RedisConnectionService());
		configure(new RedisProcessService());
		configure(new RedisContextService());
		configure(new RedisQueueService());
		configure(new RedisTopicService());
		configure(new QueueSchedulerService());
		configure(new RedisTriggerService());
	}
}
