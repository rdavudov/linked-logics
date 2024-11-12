//package io.linkedlogics.config;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.util.Map;
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import io.linkedlogics.config.LinkedLogicsConfiguration;
//
//public class ConfigurationTests {
//	
//	Map<String, Object> configuration;
//	
//	@BeforeEach
//	public void setUp() throws Exception {
//		LinkedLogicsConfiguration.config = LinkedLogicsConfiguration.load("config.yaml");;
//	}
//	
//	@Test
//	@DisplayName("get config with simple key")
//	public void shoudFindSimpleKey() {
//		Optional<Object> value = LinkedLogicsConfiguration.getConfig("key1");
//		assertThat(value).isPresent();
//	}
//	
//	@Test
//	@DisplayName("get config with missing key")
//	public void shoudNotFindSimpleKey() {
//		Optional<Object> value = LinkedLogicsConfiguration.getConfig("keyX");
//		assertThat(value).isEmpty();
//	}
//	
//	@Test
//	@DisplayName("get config with multiple key")
//	public void shoudFindMultipleKey() {
//		Optional<Object> value = LinkedLogicsConfiguration.getConfig("obj1.obj2.key1");
//		assertThat(value).isPresent();
//	}
//	
//	@Test
//	@DisplayName("get config with missing multiple key")
//	public void shoudNotFindMultipleKey() {
//		Optional<Object> value = LinkedLogicsConfiguration.getConfig("obj1.objX.key1");
//		assertThat(value).isEmpty();
//	}
//	
//	@Test
//	@DisplayName("check config with simple key")
//	public void shoudCheckSimpleKey() {
//		assertThat(LinkedLogicsConfiguration.containsConfig("key1")).isTrue();
//	}
//	
//	@Test
//	@DisplayName("check config with missing key")
//	public void shoudNotCheckSimpleKey() {
//		assertThat(LinkedLogicsConfiguration.containsConfig("keyX")).isFalse();
//	}
//	
//	@Test
//	@DisplayName("get default with missing key")
//	public void shoudFindDefaultMissingKey() {
//		Object value = LinkedLogicsConfiguration.getConfigOrDefault("keyX", "valueX");
//		assertThat(value).isEqualTo("valueX");
//	}
//	
//	@Test
//	@DisplayName("get default with missing key")
//	public void shoudNotFindDefaultSimpleKey() {
//		Object value = LinkedLogicsConfiguration.getConfigOrDefault("key1", "valueX");
//		assertThat(value).isEqualTo("value1");
//	}
//}
