package io.linkedlogics.kafka.service;

import java.time.Duration;
import java.util.Collections;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.context.Context;
import io.linkedlogics.kafka.service.config.KafkaConnectionServiceConfig;
import io.linkedlogics.service.ConsumerService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.config.ServiceConfiguration;
import io.linkedlogics.service.task.ProcessorTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaConsumerService implements ConsumerService, Runnable {
	private Thread consumer;
	private boolean isRunning;
	private Consumer<String, String> kafkaConsumer;
	private AdminClient kafkaClient;
	private KafkaConnectionServiceConfig config = new ServiceConfiguration().getConfig(KafkaConnectionServiceConfig.class);
	
	public KafkaConsumerService() {
		KafkaConnectionService connectionService = new KafkaConnectionService();
		kafkaClient = connectionService.getAdmin();
		NewTopic topic = new NewTopic(LinkedLogics.getApplicationName(), config.getPartitions(1).intValue(), config.getReplication(2).shortValue());
		kafkaClient.createTopics(Collections.singleton(topic));
		kafkaConsumer = connectionService.getConsumer(LinkedLogics.getApplicationName());
	}
	
	@Override
	public void start() {
		consumer = new Thread(this);
		consumer.start();
	}

	@Override
	public void stop() {
		isRunning = false;
		if (consumer != null) {
			consumer.interrupt();
		}
	}

	@Override
	public void run() {
		isRunning = true;
		
		while (isRunning) {
			try {
				ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
				kafkaConsumer.commitSync();
				
				for (ConsumerRecord<String, String> record : records) {
					ObjectMapper mapper = ServiceLocator.getInstance().getMapperService().getMapper();
					try {
						consume(mapper.readValue(record.value(), Context.class));
					} catch (Exception e) {
						log.error(e.getLocalizedMessage(), e);
					}
				}
			} catch (Exception e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}
	}

	@Override
	public void consume(Context context) {
		ServiceLocator.getInstance().getProcessorService().process(new ProcessorTask(context));
	}
}
