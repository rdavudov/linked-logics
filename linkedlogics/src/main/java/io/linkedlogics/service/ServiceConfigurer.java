package io.linkedlogics.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class ServiceConfigurer {
	protected Map<Class<?>, LinkedLogicsService> services = new HashMap<>();
	
	protected Map<Class<?>, LinkedLogicsService> getServices() {
		return services;
	}
	
	public ServiceConfigurer configure(LinkedLogicsService service) {
		getLinkedInterfaces(service).forEach(c -> services.put(c, service));
		return this;
	}
	
	public ServiceConfigurer configure(ServiceConfigurer configurer) {
		this.services.putAll(configurer.getServices());
		return this;
	}
	
	public Stream<Class<?>> getLinkedInterfaces(LinkedLogicsService service) {
		return getAllInterfaces(service.getClass()).stream().filter(i -> LinkedLogicsService.class.isAssignableFrom(i));
	}
	
	public static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
		Set<Class<?>> interfaces = new HashSet<>();
		for (Class<?> i : clazz.getInterfaces()) {
			interfaces.add(i);
		}
		if (clazz.getSuperclass() != null) {
			interfaces.addAll(getAllInterfaces(clazz.getSuperclass()));
		}
		return interfaces;
	}
}
