package io.linkedlogics.redis;

import java.util.List;

import io.linkedlogics.service.LinkedLogicsService;
import io.linkedlogics.service.ServiceProvider;
import io.linkedlogics.service.common.QueueConsumerService;
import io.linkedlogics.service.common.QueuePublisherService;
import io.linkedlogics.service.common.QueueSchedulerService;
import io.linkedlogics.redis.service.RedisContextService;
import io.linkedlogics.redis.service.RedisProcessService;
import io.linkedlogics.redis.service.RedisQueueService;
import io.linkedlogics.redis.service.RedisTopicService;
import io.linkedlogics.redis.service.RedisTriggerService;

public class RedisServices extends ServiceProvider {
	@Override
	public List<LinkedLogicsService> getMessagingServices() {
		return List.of(new RedisQueueService(), new RedisTopicService(), new QueuePublisherService(), new QueueConsumerService());
	}

	@Override
	public List<LinkedLogicsService> getSchedulingServices() {
		return List.of(new QueueSchedulerService());
	}

	@Override
	public List<LinkedLogicsService> getStoringServices() {
		return List.of(new RedisContextService(), new RedisTriggerService(), new RedisProcessService());
	}
}
