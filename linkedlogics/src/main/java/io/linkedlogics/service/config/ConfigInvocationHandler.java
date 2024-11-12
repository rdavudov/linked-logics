package io.linkedlogics.service.config;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import io.linkedlogics.config.LinkedLogicsConfiguration;
import lombok.AllArgsConstructor;

import static io.linkedlogics.config.LinkedLogicsConfiguration.LINKEDLOGICS;

@AllArgsConstructor
public class ConfigInvocationHandler implements InvocationHandler {
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Prefix prefix = method.getDeclaringClass().getAnnotation(Prefix.class);
		String prefixKey = prefix != null ? prefix.value() : "";
		
		Config annotation = method.getAnnotation(Config.class);
		Optional<Object> value = LinkedLogicsConfiguration.get(join(LINKEDLOGICS, prefixKey, annotation.key()));		
			
		Object configValue = null;
		
		if (value.isPresent()) {
			configValue = value.get();
		} else if (annotation.required()) {
			throw new RuntimeException(join(LINKEDLOGICS, prefixKey, annotation.key()) + " is missing");
		} else if (method.getParameterCount() > 0){
			configValue = args[0];
		} else {
			configValue = null;
		}
 		
		if (method.getReturnType() == Optional.class) {
			return Optional.ofNullable(configValue);
		}
		
		if (configValue instanceof String) {
			if (method.getReturnType() == Integer.class) {
				return Integer.parseInt(configValue.toString());
			} else if (method.getReturnType() == Long.class) {
				return Long.parseLong(configValue.toString());
			} else if (method.getReturnType() == Double.class) {
				return Double.parseDouble(configValue.toString());
			} else if (method.getReturnType() == Boolean.class) {
				return Boolean.parseBoolean(configValue.toString());
			}
		}
		
		return configValue;
	}
	
	private String join(String... keys) {
		return Arrays.stream(keys).collect(Collectors.joining("."));
	}
}
