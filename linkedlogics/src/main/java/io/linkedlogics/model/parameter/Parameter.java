package io.linkedlogics.model.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import io.linkedlogics.annotation.Input;
import io.linkedlogics.context.Context;
import io.linkedlogics.exception.MissingInputParameterError;
import lombok.Getter;

@Getter
public abstract class Parameter {
	protected String name;
	protected Class<?> type;
	protected boolean required;
	protected boolean returned;
	
	public Parameter(String name, Class<?> type, boolean required, boolean returned) {
		super();
		this.name = name;
		this.type = type;
		this.required = required;
		this.returned = returned;
	}

	public Object getParameterValue(Context context) {
		Object value = getValue(context);
		if (value == null && isRequired()) {
			throw new MissingInputParameterError(getName());
		}
		
		if (getType() != null && Optional.class.isAssignableFrom(getType())) {
			return Optional.ofNullable(value);
		}
		
		return value;
	}

	protected abstract Object getValue(Context context);

	public static Parameter[] initParameters(Method method) {
		Parameter[] parameters = new Parameter[method.getParameterCount()];
		Type[] types = method.getGenericParameterTypes();
		
		for (int i = 0; i < method.getParameterCount(); i++) {
			parameters[i] = initParameter(types[i], method.getParameters()[i].getAnnotations());
		}

		return parameters;
	}
	
	public static Parameter initParameter(Type type, Annotation[] annotations) {
		if (annotations.length == 0) {
			return new NullParameter();
		} else {
			for (Annotation annotation : annotations) {
				if (annotation instanceof Input) {
					Input param = (Input) annotation ;
					if (type instanceof ParameterizedType) {
						ParameterizedType parameterizedType = (ParameterizedType) type;
						if (parameterizedType.getActualTypeArguments().length == 1) {
							return new CollectionParameter(param.value(), (Class<?>) parameterizedType.getRawType(), (Class<?>) parameterizedType.getActualTypeArguments()[0], param.required(), param.returned());
						} else if (parameterizedType.getActualTypeArguments().length == 2) {
							return new MapParameter(param.value(), (Class<?>) parameterizedType.getRawType(), (Class<?>) parameterizedType.getActualTypeArguments()[0], (Class<?>) parameterizedType.getActualTypeArguments()[1], param.required(), param.returned());
						}
					} else {
						return new InputParameter(param.value(), (Class<?>) type, param.required(), param.returned());
					}
					
				}
			}
			return new NullParameter();
		}
	}
}
