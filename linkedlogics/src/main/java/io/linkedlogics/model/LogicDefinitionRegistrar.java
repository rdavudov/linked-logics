package io.linkedlogics.model;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import io.linkedlogics.annotation.Logic;
import io.linkedlogics.service.ServiceLocator;

public class LogicDefinitionRegistrar {

	public static void register(Object logics) {
		if (logics.getClass().equals(Class.class)) {
			registerClass((Class<?>) logics);
		} else {
			registerObject(logics);
		}
	}

	protected static void registerObject(Object logicObject) {
		Arrays.stream(logicObject.getClass().getDeclaredMethods())
		.filter(m -> m.getAnnotation(Logic.class) != null)
		.map(m -> new LogicDefinition(m.getAnnotation(Logic.class), m, logicObject))
		.forEach(LogicDefinitionRegistrar::addLogic);
	}

	protected static void registerClass(Class<?> logicClass) {
		Arrays.stream(logicClass.getDeclaredMethods())
		.filter(m -> Modifier.isStatic(m.getModifiers()))
		.filter(m -> m.getAnnotation(Logic.class) != null)
		.map(m -> new LogicDefinition(m.getAnnotation(Logic.class), m, null))
		.forEach(LogicDefinitionRegistrar::addLogic);
	}
	
	protected static void addLogic(LogicDefinition logic) {
		ServiceLocator.getInstance().getLogicService().addLogic(logic);
	}
}
