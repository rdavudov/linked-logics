package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.error;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.file;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.script;
import static io.linkedlogics.LinkedLogicsBuilder.var;
import static io.linkedlogics.LinkedLogicsBuilder.verify;
import static io.linkedlogics.LinkedLogicsBuilder.when;
import static io.linkedlogics.test.LinkedLogicsTest.assertContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.annotation.Input;
import io.linkedlogics.annotation.Logic;
import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextBuilder;
import io.linkedlogics.context.Status;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.test.LinkedLogicsExtension;
import io.linkedlogics.test.service.TestContextService;

@ExtendWith(LinkedLogicsExtension.class)
public class ScriptProcess1Tests {

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_1").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v3");
		assertThat(ctx.getParams().get("text")).isEqualTo("3 items");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").isExecuted();
		assertContext().when("4").isExecuted();
	}

	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3"))
				.add(script("result = list.size() + ' items'").returnAs("text"))
				.build();
	}
	
	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_2").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v3");
		assertThat(ctx.getParams().get("text")).isEqualTo("3 items");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").isExecuted();
		assertContext().when("4").asVerify().isNotVerified();
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3"))
				.add(verify(when("false")).elseFailWithCode(-1).handle(error().using(script("result = list.size() + ' items'").returnAs("text"))))
				.build();
	}
	
	@Test
	public void testScenario3() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_3").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("John Doe", "New York", "NY");
		assertThat(ctx.getParams().get("text")).isEqualTo("3 items");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").isExecuted();
		assertContext().when("4").isExecuted();
		assertContext().when("5").isExecuted();
	}

	public static ProcessDefinition scenario3() {
		String script = "import groovy.json.JsonSlurper \n"
				+ "def jsonString = '{\"name\": \"John Doe\", \"age\": 30, \"address\": {\"city\": \"New York\", \"state\": \"NY\"}}' \n"
				+ "def json = new JsonSlurper().parseText(jsonString)\n"
				+ "return json";
		
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(script(script).returnAsMap())
				.add(logic("INSERT").input("list", expr("list")).input("val", var("name")))
				.add(logic("INSERT").input("list", expr("list")).input("val", var("address.city")))
				.add(logic("INSERT").input("list", expr("list")).input("val", var("address.state")))
				.add(script("result = list.size() + ' items'").returnAs("text"))
				.build();
	}

	@Test
	public void testScenario4() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_4").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("John Doe", "New York", "NY");
		assertThat(ctx.getParams().get("text")).isEqualTo("3 items");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").isExecuted();
		assertContext().when("4").isExecuted();
		assertContext().when("5").isExecuted();
	}

	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(script(file("scripts/script.groovy")).returnAsMap())
				.add(logic("INSERT").input("list", expr("list")).input("val", var("name")))
				.add(logic("INSERT").input("list", expr("list")).input("val", var("address.city")))
				.add(logic("INSERT").input("list", expr("list")).input("val", var("address.state")))
				.add(script("result = list.size() + ' items'").returnAs("text"))
				.build();
	}
	
	@Test
	public void testScenario5() {
		assertThatThrownBy(() -> {
			createProcess("SIMPLE_SCENARIO_5", 0)
			.add(script(file("scripts/script.groovy")).returnAsMap())
			.add(logic("INSERT").input("list", expr("list")).input("val", var("name")))
			.add(logic("INSERT").input("list", expr("list")).input("val", var("address.city")))
			.add(logic("INSERT").input("list", expr("list")).input("val", var("address.state")))
			.add(script("result = list.size() +  items'").returnAs("text"))
			;
		}).hasMessageContaining("syntax error");
	}
	
	@Logic(id = "INSERT", version = 1)
	public static void insert(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.add(value);
	}

	@Logic(id = "INSERT", version = 0)
	public static void insertv2(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.add(value.toUpperCase());
	}

	@Logic(id = "REMOVE")
	public static void remove(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.remove(value);
	}

	@Logic(id = "PRINT")
	public static void print(@Input("list") List<String> list) {
		System.out.println("List = " + list);
	}
}
