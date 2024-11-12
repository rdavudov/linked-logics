package io.linkedlogics.config;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LinkedLogicsConfiguration {
	public static final String LINKEDLOGICS = "linkedlogics";
	private static final String DEFAULT_CONFIG_FILE = "linkedlogics.yaml";
	static YamlConfig config;

	static {
		try {
			String configFile = System.getProperty("linkedlogics.config", DEFAULT_CONFIG_FILE);
			config = new YamlConfig(configFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Optional<Object> get(String key) {
		if (getEnv(key) != null) {
			return Optional.of(getEnv(key));
		}
		
		return Optional.ofNullable(config.getNested(key));
	}

	public static Object getOrDefault(String key, Object defaultValue) {
		if (getEnv(key) != null) {
			return getEnv(key);
		}
		
		return Optional.ofNullable(config.getNested(key)).orElse(defaultValue);
	}
	
	public static Object getOrThrow(String key, String message) {
		if (getEnv(key) != null) {
			return getEnv(key);
		}
		
		return Optional.ofNullable(config.getNested(key)).orElseThrow(() -> new NullPointerException(message));
	}
	
	private static String getEnv(String key) {
		String envKey = key.toUpperCase().replace('.', '_').replace('-', '_');
		String envValue =  System.getenv(envKey);
		return envValue;
	}
}
