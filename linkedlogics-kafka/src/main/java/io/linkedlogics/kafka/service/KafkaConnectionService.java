package io.linkedlogics.kafka.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.kafka.service.config.KafkaConnectionServiceConfig;
import io.linkedlogics.service.LinkedLogicsService;
import io.linkedlogics.service.config.ServiceConfiguration;

public class KafkaConnectionService implements LinkedLogicsService {
	
	private KafkaConnectionServiceConfig config = new ServiceConfiguration().getConfig(KafkaConnectionServiceConfig.class);
	
	public Producer<String, String> getProducer() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
		props.put(ProducerConfig.ACKS_CONFIG, "1");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 3600000);
		props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 10000);
		props.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 1000);
		props.put(ProducerConfig.METADATA_MAX_AGE_CONFIG, 4000);
		props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 50000000);
		Producer<String, String> producer = new KafkaProducer<>(props);
		return producer;
	}

	public Consumer<String, String> getConsumer(String queue) {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
		props.put(ConsumerConfig.GROUP_ID_CONFIG, LinkedLogics.getApplicationName());
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, config.getMaxPollCount().orElse(1));
		props.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 3600000);
		props.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, 10000);
		props.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 1000);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		props.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, 4000);
		props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 50000000);
		
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList(queue));
		return consumer;
	}
	
	public AdminClient getAdmin() {
		Map<String, Object> props = new HashMap<>();
		props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
		return AdminClient.create(props);
	}
}
