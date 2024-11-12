package io.linkedlogics;

import java.util.Optional;
import java.util.UUID;

import io.linkedlogics.config.LinkedLogicsConfiguration;
import io.linkedlogics.context.Context;
import io.linkedlogics.model.LogicDefinitionRegistrar;
import io.linkedlogics.model.ProcessDefinitionRegistrar;
import io.linkedlogics.service.ServiceConfigurer;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.local.LocalServiceConfigurer;
import io.linkedlogics.service.task.CancelTask;
import io.linkedlogics.service.task.StartTask;

public class LinkedLogics {
	public static final String INSTANCE_UUID = UUID.randomUUID().toString();
	
	static {
		configure(new LocalServiceConfigurer());
	}
	
	public static void configure(ServiceConfigurer configurer) {
		ServiceLocator.getInstance().configure(configurer);
	}
	
	public static void registerLogic(Object logic) {
		LogicDefinitionRegistrar.register(logic);
	}
	
	public static void registerProcess(Object process) {
		ProcessDefinitionRegistrar.register(process);
	}
	
	public static void shutdown() {
		ServiceLocator.getInstance().shutdown();
	}
	
	public static void launch() {
		ServiceLocator.getInstance().start();
	}
	
	public static String start(Context context) {
		return start(context, null);
	}
	
	public static String start(Context context, LinkedLogicsCallback callback) {
		Optional.ofNullable(callback).ifPresent(c -> {
			context.setCallback(true);
			ServiceLocator.getInstance().getCallbackService().set(context.getId(), c);
		});
		
		ServiceLocator.getInstance().getContextService().set(context);
		ServiceLocator.getInstance().getProcessorService().process(new StartTask(context));
		return context.getId();
	}
	
	public static void cancel(String contextId) {
		ServiceLocator.getInstance().getContextService().get(contextId).ifPresent(c -> {
			ServiceLocator.getInstance().getProcessorService().process(new CancelTask(c));
		});
	}
	
	public static void asyncCallback(String contextId, Object result) {
		ServiceLocator.getInstance().getAsyncService().asyncCallback(contextId, result);
	}
	
	public static void asyncCallback(String contextId, Throwable error) {
		ServiceLocator.getInstance().getAsyncService().asyncCallerror(contextId, error);
	}
	
	public static String getContextId() {
		return ServiceLocator.getInstance().getAsyncService().getContextId();
	}
	
	public static String getApplicationName() {
		return (String) LinkedLogicsConfiguration.getOrThrow("linkedlogics.application.name", "missing application.name");
	}
	
	public static String getInstanceName() {
		String instance = (String) LinkedLogicsConfiguration.getOrDefault("linkedlogics.instance.name", INSTANCE_UUID); 
		return getApplicationName() + "-" + instance;
	}
}
