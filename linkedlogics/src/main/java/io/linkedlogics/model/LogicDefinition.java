package io.linkedlogics.model;

import java.lang.reflect.Method;

import io.linkedlogics.annotation.Logic;
import io.linkedlogics.model.parameter.Parameter;
import lombok.Getter;

@Getter
public class LogicDefinition {
	private final String id;
	private final int version;
	private final String description;
	
	private final Method method;
	private final Object object;
	
	private final Parameter[] parameters;
	private final String returnAs;
	private boolean isReturnAsync;
	private boolean isReturnMap;
	
	public LogicDefinition(Logic annotation, Method method, Object object) {
		this.id = annotation.id();
		this.version = annotation.version();
		this.description = annotation.description();
		this.isReturnAsync = annotation.returnAsync();
		this.isReturnMap = annotation.returnMap();
		this.method = method;
		this.object = object;
		this.parameters = Parameter.initParameters(method);
		this.returnAs = !annotation.returnAs().isEmpty() ? annotation.returnAs() : null;
	}
	
	public LogicDefinition(String id, int version, String description, boolean isReturnAsync, boolean isReturnMap, String returnAs, Method method, Object object) {
		this.id = id;
		this.version = version;
		this.description = description;
		this.isReturnAsync = isReturnAsync;
		this.isReturnMap = isReturnMap;
		this.method = method;
		this.object = object;
		this.parameters = Parameter.initParameters(method);
		this.returnAs = returnAs;
	}
}
