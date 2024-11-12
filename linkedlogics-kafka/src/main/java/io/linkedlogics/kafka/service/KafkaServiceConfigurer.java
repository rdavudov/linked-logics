package io.linkedlogics.kafka.service;

import io.linkedlogics.service.ServiceConfigurer;

public class KafkaServiceConfigurer extends ServiceConfigurer {
	public KafkaServiceConfigurer() {
		configure(new KafkaQueueService());
		configure(new KafkaConsumerService());
		configure(new KafkaPublisherService());
	}
}

