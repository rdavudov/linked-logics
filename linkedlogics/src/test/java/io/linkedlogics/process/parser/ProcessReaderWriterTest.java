package io.linkedlogics.process.parser;

import static io.linkedlogics.LinkedLogicsBuilder.branch;
import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.error;
import static io.linkedlogics.LinkedLogicsBuilder.exit;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.fail;
import static io.linkedlogics.LinkedLogicsBuilder.group;
import static io.linkedlogics.LinkedLogicsBuilder.jump;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.max;
import static io.linkedlogics.LinkedLogicsBuilder.process;
import static io.linkedlogics.LinkedLogicsBuilder.retry;
import static io.linkedlogics.LinkedLogicsBuilder.savepoint;
import static io.linkedlogics.LinkedLogicsBuilder.loop;
import static io.linkedlogics.LinkedLogicsBuilder.seconds;
import static io.linkedlogics.LinkedLogicsBuilder.verify;
import static io.linkedlogics.LinkedLogicsBuilder.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.ProcessDefinitionReader;
import io.linkedlogics.model.ProcessDefinitionWriter;
import io.linkedlogics.process.SimpleProcess1Tests;
import io.linkedlogics.service.ContextService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.local.LocalServiceConfigurer;

public class ProcessReaderWriterTest {
	private static ContextService contextService;

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(SimpleProcess1Tests.class);
		LinkedLogics.registerProcess(SimpleProcess1Tests.class);
		LinkedLogics.launch();
		contextService = ServiceLocator.getInstance().getContextService();
	}

	@Test
	public void testScenario1() {
		ProcessDefinition scenario = scenario1();
		
		String def = new ProcessDefinitionWriter(scenario).write();
		System.out.println(def);
		assertThat(def).isNotNull();
		
		ProcessDefinition process = new ProcessDefinitionReader(def).read();
		assertThat(process).isNotNull();
		assertThat(process.getId()).isEqualTo(scenario.getId());
		assertThat(process.getVersion()).isEqualTo(scenario.getVersion());
		assertThat(process.getLogics().size()).isEqualTo(scenario.getLogics().size());
	}

	@Test
	public void testScenario2() {
		ProcessDefinition scenario = scenario2();
		
		String def = new ProcessDefinitionWriter(scenario).write();
		assertThat(def).isNotNull();
		
		ProcessDefinition process = new ProcessDefinitionReader(def).read();
		assertThat(process).isNotNull();
		assertThat(process.getId()).isEqualTo(scenario.getId());
		assertThat(process.getVersion()).isEqualTo(scenario.getVersion());
		assertThat(process.getLogics().size()).isEqualTo(scenario.getLogics().size());
	}
	
	@Test
	public void testScenario3() {
		ProcessDefinition scenario = scenario3();
		
		String def = new ProcessDefinitionWriter(scenario).write();
		assertThat(def).isNotNull();
		
		ProcessDefinition process = new ProcessDefinitionReader(def).read();
		assertThat(process).isNotNull();
		assertThat(process.getId()).isEqualTo(scenario.getId());
		assertThat(process.getVersion()).isEqualTo(scenario.getVersion());
		assertThat(process.getLogics().size()).isEqualTo(scenario.getLogics().size());
	}


	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0).inputs(constants())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3"))
				.build();
	}
	
	public static Map<String, Object> constants() {
		return new HashMap<>() {{
			put("CONFIG_A", Map.of("key1", "value1", "key2", "value2"));
			put("CONFIG_B", Map.of("key1", "value1", "key2", Map.of("key21", "value21")));
			put("CONFIG_C", Map.of("key1", Map.of("key11", "value11"), "key2", Map.of("key21", "value21")));
		}};
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(group(
				branch(expr("a > 5"), 
						logic("INSERT")
						.input("list", expr("list")).input("val", "v1").input("number", 2020)
						.delayed(5)
						.handle(error().withCodes(100,101).orMessages("error1", "error2").using(logic("ERROR")))
						.fork("F1")
						.join("F2")
						.label("L1")
						.retry(retry(5, 3).codes(100).messages("error1"))
						.timeout(5)
						),
					exit(),
					fail().withCode(200).andMessage("failure"),
					jump("L1"),
					savepoint(),
					verify(expr("false")).elseFailWithCode(100).andMessage("failure"),
					process("SIMPLE_SCENARIO_1", 0)
				)).build();

	}
	
	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_2", 0).input("list", expr("list")).input("val", "v1").input("number", 2020)
				.add(logic("INSERT").inputs("list", expr("list"), "val", "v1"))
				.add(logic("INSERT").inputs("list", expr("list"), "val", "v1")
						.retry(retry(max(5), seconds(3)).codes(100,234).messages("error1","dfgdg"))
						.delayed(5)
						.fork("F1")
						.join("F0")
						.timeout(5)
						.label("L1")
						.handle(error().withCodes(100,101).orMessages("error1", "error2").using(logic("ERROR")))
						)
				.add(exit())
				.add(fail().withCode(200).andMessage("failure"))
				.add(loop(expr("true"), logic("INSERT")).fork())
				.add(loop(expr("true"), logic("INSERT")).fork("F2"))
				.add(branch(when("account == 222"), logic("INSERT").inputs("list", expr("list"), "val", "v1")))
				.add(group(
					branch(expr("a > 5"), 
							logic("INSERT")
							.input("list", expr("list")).input("val", "v1").input("number", 2020)
							.delayed(5)
							.handle(error().withCodes(100,101).orMessages("error1", "error2").using(logic("ERROR")))
							.fork("F1")
							.join("F2")
							.label("L1")
							.retry(retry(max(5), seconds(3)).codes(100).messages("error1"))
							.timeout(5)
							),
					exit(),
					fail().withCode(200).andMessage("failure"),
					jump("L1"),
					savepoint(),
					verify(expr("false")).elseFailWithCode(100).andMessage("failure")
					))
				.build();
	}
}
