package io.linkedlogics.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.linkedlogics.service.LinkedLogicsService;
import io.linkedlogics.service.ServiceConfigurer;
import io.linkedlogics.service.config.ServiceConfig;

public class ServiceConfigurerTests {
	
	@Test
	@DisplayName("get service instance by multiple interfaces")
	public void shouldReturnServiceByTwoInterfaces() {
		ServiceABImpl1 serviceAB = new ServiceABImpl1();
		
		ServiceConfigurer configurer = new ServiceConfigurer();
		configurer.configure(serviceAB);
		
		assertThat(configurer.getServices().get(ServiceA.class)).isEqualTo(serviceAB);
		assertThat(configurer.getServices().get(ServiceB.class)).isEqualTo(serviceAB);
		assertThat(configurer.getServices().get(ServiceC.class)).isNull();
	}
	
	@Test
	@DisplayName("get service instance by interface")
	public void shouldReturnServiceByInterface() {
		ServiceA serviceA = new ServiceAImpl1();
		ServiceB serviceB = new ServiceBImpl1();
		ServiceC serviceC = new ServiceCImpl1();
		
		ServiceConfigurer configurer = new ServiceConfigurer();
		configurer.configure(serviceA);
		configurer.configure(serviceB);
		configurer.configure(serviceC);
		
		assertThat(configurer.getServices().get(ServiceA.class)).isEqualTo(serviceA);
		assertThat(configurer.getServices().get(ServiceB.class)).isEqualTo(serviceB);
		assertThat(configurer.getServices().get(ServiceC.class)).isEqualTo(serviceC);
	}
	
	@Test
	@DisplayName("get service instance by multiple configurers")
	public void shouldOverwriteServiceByConfigurer() {
		
		ServiceConfigurer configurer = new ServiceConfigurer1().configure(new ServiceConfigurer2());
		
		assertThat(configurer.getServices().get(ServiceA.class).getClass()).isEqualTo(ServiceAImpl2.class);
		assertThat(configurer.getServices().get(ServiceB.class).getClass()).isEqualTo(ServiceBImpl2.class);
		assertThat(configurer.getServices().get(ServiceC.class).getClass()).isEqualTo(ServiceCImpl1.class);
	}
	
	private class ServiceConfigurer1 extends ServiceConfigurer {
		public ServiceConfigurer1() {
			configure(new ServiceAImpl1());
			configure(new ServiceBImpl1());
			configure(new ServiceCImpl1());
		}
	}
	
	private class ServiceConfigurer2 extends ServiceConfigurer {
		public ServiceConfigurer2() {
			configure(new ServiceAImpl2());
			configure(new ServiceBImpl2());
		}
	}
	
	
	private interface ServiceA extends LinkedLogicsService {

	}
	
	private interface ServiceB extends LinkedLogicsService {

	}
	
	private interface ServiceC extends LinkedLogicsService {

	}
	
	private class ServiceAImpl1 implements ServiceA {

	}
	
	private class ServiceAImpl2 implements ServiceA {

	}
	
	private class ServiceBImpl1 implements ServiceB {

	}
	
	private class ServiceBImpl2 implements ServiceB {

	}
	
	private class ServiceCImpl1 implements ServiceC {

	}
	
	private class ServiceCImpl2 implements ServiceC {

	}
	
	private class ServiceABImpl1 implements ServiceA, ServiceB {
		
	}
	
	private interface DummyServiceConfig extends ServiceConfig {
		
	}
}
