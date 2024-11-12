package io.linkedlogics.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.context.Context;
import io.linkedlogics.service.ConsumerService;
import io.linkedlogics.service.LinkedLogicsService;
import io.linkedlogics.service.PublisherService;
import io.linkedlogics.service.ServiceConfigurer;
import io.linkedlogics.service.ServiceLocator;
import lombok.Getter;

public class ServiceLocatorTests {
	
	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new ConfigurerA().configure(new ConfigurerB()));
	}
	
	@Test
	@DisplayName("should return last configured service")
	public void shouldReturnCorrectService() {
		assertThat(ServiceLocator.getInstance().getPublisherService().getClass()).isEqualTo(PublisherB.class);
		assertThat(ServiceLocator.getInstance().getConsumerService().getClass()).isEqualTo(ConsumerA.class);
	}
	
	@Test
	@DisplayName("should start and stop service")
	public void shouldStartAndStopService() {
		ServiceConfigurer configurer = new ServiceConfigurer();
		LinkedLogicsService service = Mockito.mock(LinkedLogicsService.class);
		configurer.configure(service);
		
		ServiceLocator.getInstance().configure(configurer);
		ServiceLocator.getInstance().start();
		ServiceLocator.getInstance().shutdown();
		
		verify(service, times(1)).start();
		verify(service, times(1)).stop();
	}
	
	@Test
	@DisplayName("should start and stop service")
	public void shouldStartAndStopServiceOnce() {
		ServiceConfigurer configurer = new ServiceConfigurer();
		PublisherConsumer service = new PublisherConsumer();
		configurer.configure(service);
		
		ServiceLocator.getInstance().configure(configurer);
		ServiceLocator.getInstance().start();
		ServiceLocator.getInstance().shutdown();
		
		assertThat(service.getStartCounter()).isEqualTo(1);
		assertThat(service.getStopCounter()).isEqualTo(1);
	}
	
	public static class ConfigurerA extends ServiceConfigurer {
		public ConfigurerA() {
			configure(new PublisherA());
			configure(new ConsumerA());
		}
	}
	
	public static class ConfigurerB extends ServiceConfigurer {
		public ConfigurerB() {
			configure(new PublisherB());
		}
	}
	
	public static class PublisherA implements PublisherService {

		@Override
		public void  publish(Context context) {

		}
	}
	
	public static class PublisherB implements PublisherService {
		
		@Override
		public void publish(Context context) {

		}
	}
	
	public static class ConsumerA implements ConsumerService {

		@Override
		public void consume(Context context) {

		}
	}
	
	@Getter
	public static class PublisherConsumer implements PublisherService, ConsumerService {
		private int startCounter;
		private int stopCounter;
		
		@Override
		public void start() {
			startCounter++;
		}

		@Override
		public void stop() {
			stopCounter++;
		}

		@Override
		public void consume(Context context) {
			
		}

		@Override
		public void publish(Context context) {
			
		}
	}
}
