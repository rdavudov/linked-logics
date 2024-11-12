package io.linkedlogics.service.config;

import java.lang.reflect.Proxy;

public class ServiceConfiguration {
	public <T> T getConfig(Class<T> configClass) {
		try {
			return (T) Proxy.newProxyInstance(configClass.getClassLoader(), new Class[] {configClass}, new ConfigInvocationHandler());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
