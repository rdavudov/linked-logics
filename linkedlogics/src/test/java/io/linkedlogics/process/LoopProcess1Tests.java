package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.log;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.loop;
import static io.linkedlogics.LinkedLogicsBuilder.script;
import static io.linkedlogics.LinkedLogicsBuilder.when;
import static io.linkedlogics.test.LinkedLogicsTest.assertContext;
import static org.assertj.core.api.Assertions.assertThat;

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
public class LoopProcess1Tests {

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_1").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(6);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2");
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").asLoop().isIterated(3);
	}


	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(loop(when("list.size() < 6"), 
						logic("INSERT").input("list", expr("list")).input("val", "v1"),
						logic("INSERT").input("list", expr("list")).input("val", "v2")))
				.build();
	}
	
	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_2")
				.params("list", new ArrayList<>(), "source", List.of("v1", "v2", "v3")).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v3");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").asLoop().isIterated(3);
	}
	
	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(script("0").returnAs("i"))
				.add(loop(when("i < source.size()"), 
						logic("INSERT").input("list", expr("list")).input("val", expr("source[i]")),
						log("i = {i}").input("i", expr("i")),
						script("i + 1").returnAs("i")))
				.build();
	}
	
	@Test
	public void testScenario3() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_3")
				.params("list", new ArrayList<>(), "list1", List.of("a", "b", "c"), "list2", List.of("1", "2", "3")).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(9);
		assertThat(ctx.getParams().get("list")).asList().contains("a1", "a2", "a3", "b1", "b2", "b3", "c1", "c2", "c3");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").asLoop().isIterated(3);
		assertContext().when("2.2").asLoop().isIterated(9);
		
	}
	
	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(script("0").returnAs("i"))
				.add(loop(when("i < list1.size()"), 
						script("0").returnAs("j"),
						loop(when("j < list2.size()"), 
								logic("INSERT").input("list", expr("list")).input("val", expr("list1[i] + list2[j]")),
								script("j + 1").returnAs("j")),
						script("i + 1").returnAs("i")))
				.build();
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
