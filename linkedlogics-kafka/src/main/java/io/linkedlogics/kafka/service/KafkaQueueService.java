package io.linkedlogics.kafka.service;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import io.linkedlogics.kafka.service.config.KafkaConnectionServiceConfig;
import io.linkedlogics.service.QueueService;
import io.linkedlogics.service.config.ServiceConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaQueueService implements QueueService {
	private ConcurrentHashMap<String, Consumer<String, String>> consumers;
	private Producer<String, String> producer;
	private KafkaConnectionService kafka;
	private KafkaConnectionServiceConfig config = new ServiceConfiguration().getConfig(KafkaConnectionServiceConfig.class);
	private AdminClient kafkaClient;
	
	public KafkaQueueService() {
		kafka = new KafkaConnectionService();
		producer = kafka.getProducer();
		consumers = new ConcurrentHashMap<>();
		kafkaClient = kafka.getAdmin();
	}
	
	public void stop() {
		if (producer != null) {
			producer.close();
		}
		
		consumers.values().forEach(c -> c.close());
	}
	
	@Override
	public void offer(String queue, String payload) {
		ProducerRecord<String, String> record = new ProducerRecord<>(queue, payload);
        try {
			producer.send(record).get();
			producer.flush();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Optional<String> poll(String queue) {
		if (!consumers.containsKey(queue)) {
			consumers.putIfAbsent(queue, kafka.getConsumer(queue));
			NewTopic topic = new NewTopic(queue, config.getPartitions(1).intValue(), config.getReplication(1).shortValue());
			kafkaClient.createTopics(Collections.singleton(topic));
		}
	
		ConsumerRecords<String, String> records = consumers.get(queue).poll(Duration.ofMillis(1));
		for (ConsumerRecord<String, String> record : records) {
			return Optional.of(record.value());
		}
		
		return Optional.empty();
	}
}
