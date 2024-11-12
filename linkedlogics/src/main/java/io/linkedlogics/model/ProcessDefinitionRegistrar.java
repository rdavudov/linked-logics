package io.linkedlogics.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import io.linkedlogics.service.ServiceLocator;

public class ProcessDefinitionRegistrar {
	
	public static void register(Object logics) {
		if (logics.getClass().equals(Class.class)) {
			registerClass((Class<?>) logics);
		} else {
			registerObject(logics);
		}
	}
	
	protected static void registerObject(Object processObject) {
		Arrays.stream(processObject.getClass().getDeclaredMethods())
		.filter(m -> ProcessDefinition.class.isAssignableFrom(m.getReturnType()))
		.map(m -> {
			try {
				return (ProcessDefinition) m.invoke(processObject);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}).forEach(ProcessDefinitionRegistrar::addProcessDefinition);
	}
	
	protected static void registerClass(Class<?> processClass) {
		Arrays.stream(processClass.getDeclaredMethods())
		.filter(m -> Modifier.isStatic(m.getModifiers()))
		.filter(m -> ProcessDefinition.class.isAssignableFrom(m.getReturnType()))
		.map(m -> {
			try {
				return (ProcessDefinition) m.invoke(null);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getCause());
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}).forEach(ProcessDefinitionRegistrar::addProcessDefinition);
	}
	
	protected static void addProcessDefinition(ProcessDefinition process) {
		ServiceLocator.getInstance().getProcessService().addProcess(process);
	}
}
