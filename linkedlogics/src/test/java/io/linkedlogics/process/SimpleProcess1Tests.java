package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.group;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
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
public class SimpleProcess1Tests {

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
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").isExecuted();
	}



	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3"))
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
		assertThat(ctx.getParams().get("list")).asList().hasSize(9);
		assertThat(ctx.getParams().get("list")).asList().contains("v11", "v12", "v13","v21", "v22", "v23","v31", "v32", "v33");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").isExecuted();
	}



	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v11"),
						logic("INSERT").input("list", expr("list")).input("val", "v12"),
						logic("INSERT").input("list", expr("list")).input("val", "v13")))
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v21"),
						logic("INSERT").input("list", expr("list")).input("val", "v22"),
						logic("INSERT").input("list", expr("list")).input("val", "v23")))
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v31"),
						logic("INSERT").input("list", expr("list")).input("val", "v32"),
						logic("INSERT").input("list", expr("list")).input("val", "v33")))
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
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v1");
		
		assertContext().when("1").isExecuted();
		assertContext().when("1.1").isExecuted();
		assertContext().when("1.1.1").isExecuted();
		assertContext().when("1.1.1.1").isExecuted();
		assertContext().when("1.1.1.1.1").isExecuted();
		assertContext().when("1.1.1.1.1.1").isExecuted();
		assertContext().when("1.1.1.1.1.1.1").isExecuted();
	}



	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(group(group(group(group(group(group(logic("INSERT").input("list", expr("list")).input("val", "v1"))))))))
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
		assertThat(ctx.getParams().get("list")).asList().hasSize(6);
		assertThat(ctx.getParams().get("list")).asList().contains("v1","v2","v3","v4","v5","v6");
		
		assertContext().whenAll("1", "1.1", "1.2", "1.2.1", "1.2.2", "1.2.2.1", "1.2.2.2", "1.2.2.2.1", "1.2.2.2.2", "1.2.2.2.2.1", "1.2.2.2.2.2", "1.2.2.2.2.2.1").areExecuted();
	}



	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v1"),
						group(logic("INSERT").input("list", expr("list")).input("val", "v2"),
								group(logic("INSERT").input("list", expr("list")).input("val", "v3"),
										group(logic("INSERT").input("list", expr("list")).input("val", "v4"),
												group(logic("INSERT").input("list", expr("list")).input("val", "v5"),
														group(logic("INSERT").input("list", expr("list")).input("val", "v6"))))))))
				.build();
	}

	@Test
	public void testScenario5() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_5").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(5);
		assertThat(ctx.getParams().get("list")).asList().contains("v11", "v13", "v31", "v32", "v33");
		
		assertContext().when("1").isExecuted();
		assertContext().when("1.1").isExecuted();
		assertContext().when("1.2").isNotExecuted();
		assertContext().when("1.3").isExecuted();
		assertContext().when("2").isNotExecuted();
		assertContext().when("2.1").isNotExecuted();
		assertContext().when("2.2").isNotExecuted();
		assertContext().when("2.3").isNotExecuted();
		assertContext().when("3").isExecuted();
		assertContext().when("3.1").isExecuted();
		assertContext().when("3.2").isExecuted();
		assertContext().when("3.3").isExecuted();
	}



	public static ProcessDefinition scenario5() {
		return createProcess("SIMPLE_SCENARIO_5", 0)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v11"),
						logic("INSERT").input("list", expr("list")).input("val", "v12").disabled(),
						logic("INSERT").input("list", expr("list")).input("val", "v13")))
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v21"),
						logic("INSERT").input("list", expr("list")).input("val", "v22"),
						logic("INSERT").input("list", expr("list")).input("val", "v23")).disabled())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v31"),
						logic("INSERT").input("list", expr("list")).input("val", "v32"),
						logic("INSERT").input("list", expr("list")).input("val", "v33")))
				.build();
	}

	@Test
	public void testScenario6() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_6").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "V2", "V3");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").isExecuted();
	}



	public static ProcessDefinition scenario6() {
		return createProcess("SIMPLE_SCENARIO_6", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(logic("INSERT", 0).input("list", expr("list")).input("val", "v2"))
				.add(logic("INSERT", 0).input("list", expr("list")).input("val", "v3"))
				.build();
	}

	@Test
	public void testScenario7() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_7").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v3");
		assertThat(ctx.getParams().containsKey("out")).isTrue();
		assertThat(ctx.getParams().get("out")).isEqualTo("o2");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").isExecuted();
	}



	public static ProcessDefinition scenariol7() {
		return createProcess("SIMPLE_SCENARIO_7", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").outputs("out", "o2"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3"))
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
