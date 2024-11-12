package io.linkedlogics.jdbc.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.linkedlogics.jdbc.repository.TopicRepository;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.TopicService;
import io.linkedlogics.test.LinkedLogicsExtension;
import io.linkedlogics.test.LinkedLogicsRegister;

@ExtendWith(LinkedLogicsExtension.class)
@LinkedLogicsRegister(serviceConfigurerClasses = JdbcServiceConfigurer.class)
public class TopicServiceTests {
	private static final String TOPIC = "t1";
	
	@Test
	public void shouldOfferAndConsume() {
		TopicService topicService = ServiceLocator.getInstance().getService(TopicService.class);
		
		topicService.offer(TOPIC, "hello");
		
		Optional<String> message = topicService.poll(TOPIC);
		assertThat(message).isPresent();
		assertThat(message.get()).isEqualTo("hello");
		
		message = topicService.poll(TOPIC);
		assertThat(message).isEmpty();
		
		TopicRepository repository = new TopicRepository(new JdbcConnectionService().getDataSource());
		message = repository.get(TOPIC, "other_consumer_id");
		assertThat(message).isPresent();
	}
}
