package io.linkedlogics.service;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceProvider {
	public List<LinkedLogicsService> getStoringServices() {
		return List.of();
	}
	
	public List<LinkedLogicsService> getMessagingServices() {
		return List.of();
	}
	
	public List<LinkedLogicsService> getSchedulingServices() {
		return List.of();
	}
	
	public List<LinkedLogicsService> getProcessingServices() {
		return List.of();
	}
	
	public List<LinkedLogicsService> getMonitoringServices() {
		return List.of();
	}
	
	public List<LinkedLogicsService> getEvaluatingServices() {
		return List.of();
	}
	
	public List<LinkedLogicsService> getTrackingServices() {
		return List.of();
	}
	
	public List<LinkedLogicsService> getServices() {
		return List.of(getStoringServices(),
						getMessagingServices(),
						getSchedulingServices(),
						getProcessingServices(),
						getMonitoringServices(),
						getEvaluatingServices(),
						getTrackingServices())
				.stream()
				.flatMap(l -> l.stream())
				.collect(Collectors.toList());
	}
}
