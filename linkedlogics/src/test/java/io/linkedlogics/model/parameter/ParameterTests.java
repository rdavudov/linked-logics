package io.linkedlogics.model.parameter;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.linkedlogics.annotation.Input;
import io.linkedlogics.annotation.Logic;
import io.linkedlogics.model.parameter.InputParameter;
import io.linkedlogics.model.parameter.NullParameter;
import io.linkedlogics.model.parameter.Parameter;

public class ParameterTests {
	
	@Test
	@DisplayName("should parse a methods parameters")
	public void shouldParseMethodDummy1() {
		Method method = Arrays.stream(this.getClass().getDeclaredMethods()).filter(m -> m.getName().equals("dummy1")).findFirst().get();
		Parameter[] parameters = Parameter.initParameters(method);
		assertThat(parameters).hasSize(3);
		assertThat(parameters[0].getClass()).isEqualTo(InputParameter.class);
		assertThat(parameters[1].getClass()).isEqualTo(InputParameter.class);
		assertThat(parameters[2].getClass()).isEqualTo(NullParameter.class);
	}
	
	@Test
	@DisplayName("should parse a methods parameters")
	public void shouldParseMethodDummy2() {
		Method method = Arrays.stream(this.getClass().getDeclaredMethods()).filter(m -> m.getName().equals("dummy2")).findFirst().get();
		Parameter[] parameters = Parameter.initParameters(method);
		assertThat(parameters).hasSize(2);
		assertThat(parameters[0].getClass()).isEqualTo(InputParameter.class);
		assertThat(parameters[0].getType()).isEqualTo(Long.class);
		assertThat(parameters[0].isRequired()).isTrue();
		assertThat(parameters[1].getClass()).isEqualTo(NullParameter.class);
	}
	
	@Logic(id = "DUMMY_1", description = "dummy logic", returnAs = "dummy_result")
	public String dummy1(@Input("firstname") String firstName, @Input("lastname") String lastName, int age) {
		return "";
	}
	
	@Logic(id = "DUMMY_2", description = "dummy logic")
	public void dummy2(@Input(value = "account_id", required = true) Long accountId, String accountHolder) {

	}
}
