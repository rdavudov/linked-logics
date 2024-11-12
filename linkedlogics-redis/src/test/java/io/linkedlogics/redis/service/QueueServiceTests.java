package io.linkedlogics.redis.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.linkedlogics.redis.extension.RedisExtension;
import io.linkedlogics.service.QueueService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.test.LinkedLogicsExtension;
import io.linkedlogics.test.LinkedLogicsRegister;

@ExtendWith({LinkedLogicsExtension.class, RedisExtension.class})
@LinkedLogicsRegister(serviceConfigurerClasses = RedisServiceConfigurer.class)
public class QueueServiceTests {
	private static final String QUEUE = "q1";
	
	@Test
	public void shouldOfferAndConsume() {
		QueueService queueService = ServiceLocator.getInstance().getService(QueueService.class);
		
		queueService.offer(QUEUE, "hello");
		
		Optional<String> message = queueService.poll(QUEUE);
		assertThat(message).isPresent();
		assertThat(message.get()).isEqualTo("hello");
		
		message = queueService.poll(QUEUE);
		assertThat(message).isEmpty();
	}
}
