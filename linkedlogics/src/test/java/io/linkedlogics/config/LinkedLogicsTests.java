package io.linkedlogics.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.linkedlogics.LinkedLogics;

public class LinkedLogicsTests {
	
	@Test
	@DisplayName("get application name")
	public void shoudFindSimpleKey() {
		assertThat(LinkedLogics.getApplicationName()).isEqualTo("test");
	}
}
