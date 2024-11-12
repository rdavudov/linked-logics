package io.linkedlogics.service.local;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.annotation.Input;
import io.linkedlogics.annotation.Logic;
import io.linkedlogics.exception.AlreadyExistingError;
import io.linkedlogics.model.LogicDefinition;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.local.LocalServiceConfigurer;

public class LocalLogicServiceTests {
	public static final String DUMMY_A0 = "DUMMY_A0";
	public static final String DUMMY_A1 = "DUMMY_A1";
	public static final String DUMMY_A2 = "DUMMY_A2";
	public static final String DUMMY_B1 = "DUMMY_B1";
	public static final String DUMMY_B2 = "DUMMY_B2";
	public static final String DUMMY_DESC = "dummy desc";
	public static final String DUMMY_RETURN = "dummy result";

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(new LogicsA());
		LinkedLogics.registerLogic(LogicsB.class);
	}

	@Test
	@DisplayName("should parse logics from object")
	public void shouldParseLogicsA1FromObject() {
		Optional<LogicDefinition> definition = ServiceLocator.getInstance().getLogicService().getLogic(DUMMY_A1);

		assertThat(definition).isPresent();
		assertThat(definition.get().getVersion()).isEqualTo(2);
		assertThat(definition.get().getDescription()).isEqualTo(DUMMY_DESC);
		assertThat(definition.get().getReturnAs()).isEqualTo(DUMMY_RETURN);
	}

	@Test
	@DisplayName("should parse logics from object")
	public void shouldParseLogicsA2FromObject() {
		Optional<LogicDefinition> definition = ServiceLocator.getInstance().getLogicService().getLogic(DUMMY_A2);

		assertThat(definition).isPresent();
		assertThat(definition.get().getVersion()).isEqualTo(0);
		assertThat(definition.get().getDescription()).isEqualTo(DUMMY_DESC);
		assertThat(definition.get().getReturnAs()).isNull();
	}

	@Test
	@DisplayName("should not find missing logics from object")
	public void shouldNotParseLogicsA0FromObject() {
		Optional<LogicDefinition> definition = ServiceLocator.getInstance().getLogicService().getLogic(DUMMY_A0);

		assertThat(definition).isEmpty();
	}

	@Test
	@DisplayName("should parse logics from object")
	public void shouldParseLogicsB1FromObject() {
		Optional<LogicDefinition> definition = ServiceLocator.getInstance().getLogicService().getLogic(DUMMY_B1);

		assertThat(definition).isPresent();
		assertThat(definition.get().getVersion()).isEqualTo(0);
		assertThat(definition.get().getDescription()).isEqualTo(DUMMY_DESC);
		assertThat(definition.get().getReturnAs()).isEqualTo(DUMMY_RETURN);
	}

	@Test
	@DisplayName("should parse logics from object")
	public void shouldParseLogicsB2FromObject() {
		Optional<LogicDefinition> definition = ServiceLocator.getInstance().getLogicService().getLogic(DUMMY_B2);

		assertThat(definition).isPresent();
		assertThat(definition.get().getVersion()).isEqualTo(0);
		assertThat(definition.get().getDescription()).isEqualTo(DUMMY_DESC);
		assertThat(definition.get().getReturnAs()).isNull();
	}
	
	@Test
	@DisplayName("should not parse duplicate logics from object")
	public void shouldNotParseDuplicateLogicsFromObject() {
		assertThatThrownBy(() -> {
			LinkedLogics.registerLogic(LogicsC.class);
		}).isInstanceOf(AlreadyExistingError.class);
	}

	private static class LogicsA {
		@Logic(id = DUMMY_A1, description = DUMMY_DESC, returnAs = DUMMY_RETURN)
		public String dummya1(@Input("firstname") String firstName, @Input("lastname") String lastName, int age) {
			return "";
		}

		@Logic(id = DUMMY_A1, description = DUMMY_DESC, returnAs = DUMMY_RETURN, version = 2)
		public String dummya1v2(@Input("firstname") String firstName, @Input("lastname") String lastName, int age) {
			return "";
		}

		@Logic(id = DUMMY_A2, description = DUMMY_DESC)
		public void dummya2(@Input(value = "account_id", required = true) Long accountId, String accountHolder) {

		}
	}

	private static class LogicsB {
		@Logic(id = DUMMY_B1, description = DUMMY_DESC, returnAs = DUMMY_RETURN)
		public static String dummyb1(@Input("firstname") String firstName, @Input("lastname") String lastName, int age) {
			return "";
		}

		@Logic(id = DUMMY_B2, description = DUMMY_DESC)
		public static void dummyb2(@Input(value = "account_id", required = true) Long accountId, String accountHolder) {

		}
	}
	
	private static class LogicsC {
		@Logic(id = DUMMY_B1, description = DUMMY_DESC, returnAs = DUMMY_RETURN)
		public static String dummyb1(@Input("firstname") String firstName, @Input("lastname") String lastName, int age) {
			return "";
		}

		@Logic(id = DUMMY_B2, description = DUMMY_DESC)
		public static void dummyb2(@Input(value = "account_id", required = true) Long accountId, String accountHolder) {

		}
	}
}
