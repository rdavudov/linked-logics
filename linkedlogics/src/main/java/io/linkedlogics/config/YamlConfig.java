package io.linkedlogics.config;

import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

class YamlConfig {	
	private Map<String, Object> config;
	
	public YamlConfig(String configFile) {
		InputStream input = LinkedLogicsConfiguration.class.getClassLoader().getResourceAsStream(configFile);
		if (input == null) {
			throw new IllegalArgumentException("missing " + configFile);
		}
		
		Yaml yaml = new Yaml();
		config = yaml.load(input);
	}
	
	Object getNested(String key) {
		String[] keys = key.split("\\.");
		Map<String, Object> cursor = config;
		for (int i = 0; i < keys.length - 1; i++) {
			cursor = (Map<String, Object>) cursor.get(keys[i]);
			if (cursor == null) {
				return null;
			}
		}

		return cursor.get(keys[keys.length - 1]);
	}
}
