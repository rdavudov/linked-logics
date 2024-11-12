package io.linkedlogics.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceLocator {
	private static final ServiceLocator INSTANCE = new ServiceLocator();
	
	private Map<Class, LinkedLogicsService> services = new HashMap<>();
	
	public static ServiceLocator getInstance() {
		return INSTANCE;
	}
	
	public void configure(ServiceConfigurer configurer) {
		configurer.getServices().entrySet().forEach(e -> {
			this.services.put(e.getKey(), e.getValue());
		});
	}
	
	public <T> T getService(Class<T> serviceClass) {
		return (T) services.get(serviceClass);
	}
	
	public ConsumerService getConsumerService() {
		return getService(ConsumerService.class);
	}
	
	public PublisherService getPublisherService() {
		return getService(PublisherService.class);
	}
	
	public ProcessorService getProcessorService() {
		return getService(ProcessorService.class);
	}
	
	public SchedulerService getSchedulerService() {
		return getService(SchedulerService.class);
	}
	
	public EvaluatorService getEvaluatorService() {
		return getService(EvaluatorService.class);
	}
	
	public AsyncService getAsyncService() {
		return getService(AsyncService.class);
	}
	
	public ContextService getContextService() {
		return getService(ContextService.class);
	}
	
	public LogicService getLogicService() {
		return getService(LogicService.class);
	}
	
	public ProcessService getProcessService() {
		return getService(ProcessService.class);
	}
	
	public TriggerService getTriggerService() {
		return getService(TriggerService.class);
	}
	
	public MapperService getMapperService() {
		return getService(MapperService.class);
	}
	
	public CallbackService getCallbackService() {
		return getService(CallbackService.class);
	}
	
	public MetricService getMetricsService() {
		return getService(MetricService.class);
	}
	
	public TrackingService getTrackingService() {
		return getService(TrackingService.class);
	}
	
	public QueueService getQueueService() {
		return getService(QueueService.class);
	}
	
	public TopicService getTopicService() {
		return getService(TopicService.class);
	}
	
	public LimitService getLimitService() {
		return getService(LimitService.class);
	}
	
	public LoggingService getLoggingService() {
		return getService(LoggingService.class);
	}
	
	public void shutdown() {
		services.values().stream().distinct().sorted(Comparator.comparing(LinkedLogicsService::order).reversed()).forEach(s -> s.stop());
	}
	
	public void start() {
		log.info("Launching Linked Logics");
		services.values().stream().distinct().sorted(Comparator.comparing(LinkedLogicsService::order)).forEach(s -> s.start());
	}
	
	public void print() {
//		services.values().stream().map(s -> s.getClass().getSimpleName() + ":" + s.getConfigClass().getSimpleName()).forEach(System.out::println);
	}
}
