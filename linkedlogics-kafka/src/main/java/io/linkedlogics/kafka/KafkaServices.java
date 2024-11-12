package io.linkedlogics.kafka;

import java.util.List;

import io.linkedlogics.service.LinkedLogicsService;
import io.linkedlogics.service.ServiceProvider;
import io.linkedlogics.service.common.QueueSchedulerService;
import io.linkedlogics.kafka.service.KafkaConsumerService;
import io.linkedlogics.kafka.service.KafkaPublisherService;
import io.linkedlogics.kafka.service.KafkaQueueService;

public class KafkaServices extends ServiceProvider {
	@Override
	public List<LinkedLogicsService> getMessagingServices() {
		return List.of(new KafkaQueueService(), new KafkaConsumerService(), new KafkaPublisherService());
	}

	@Override
	public List<LinkedLogicsService> getSchedulingServices() {
		return List.of(new QueueSchedulerService());
	}
}