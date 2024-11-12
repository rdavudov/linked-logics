package io.linkedlogics.kafka.service;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.PublisherService;
import io.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaPublisherService implements PublisherService {

	private Producer<String, String> kafkaproducer;
	
	public KafkaPublisherService() {
		kafkaproducer = new KafkaConnectionService().getProducer();
	}
	
	public void stop() {
		if (kafkaproducer != null) {
			kafkaproducer.close();
		}
	}

	@Override
	public void publish(Context context) {
		ObjectMapper mapper = ServiceLocator.getInstance().getMapperService().getMapper();
		try {
			ProducerRecord<String, String> record = new ProducerRecord<>(context.getApplication(), context.getKey(), mapper.writeValueAsString(context));
			kafkaproducer.send(record);
	        kafkaproducer.flush();
		} catch (JsonProcessingException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
}
