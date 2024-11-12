package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.test.LinkedLogicsTest.assertContext;
import static io.linkedlogics.test.LinkedLogicsTest.assertInputs;
import static io.linkedlogics.test.LinkedLogicsTest.mockLogic;
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
public class MockProcess1Tests {
	
	@Test
	public void testScenario1() {
		mockLogic("MULTIPLY").returnAs("multiply_result").thenReturn(-1);
		
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_1").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 4, -1);
		
		assertContext().whenAll("1", "2", "3", "4").areExecuted();
	}

	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2))
				.add(logic("INSERT").input("list", expr("list")).input("val", 4))
				.add(logic("MULTIPLY").input("val1", 3).input("val2", 2))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.build();
	}
	
	@Test
	public void testScenario2() {
		mockLogic("MULTIPLY").when("val1 > val2").returnAs("multiply_result").thenReturn(-1);
		mockLogic("MULTIPLY").when("val2 > val1").returnAs("multiply_result").thenReturn(-2);
		
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_2").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(4);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 4, -1, -2);
		
		assertContext().whenAll("1", "2", "3", "4", "5", "6").areExecuted();
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2))
				.add(logic("INSERT").input("list", expr("list")).input("val", 4))
				.add(logic("MULTIPLY").input("val1", 3).input("val2", 2))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.add(logic("MULTIPLY").input("val1", 2).input("val2", 3))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.build();
	}
	
	@Test
	public void testScenario3() {
		mockLogic("MULTIPLY").when("val1 == val2").returnAs("multiply_result").thenReturn(-1);
		
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_3").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 4);
		
		assertContext().whenAll("1", "2", "3").areExecuted();
	}

	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2))
				.add(logic("INSERT").input("list", expr("list")).input("val", 4))
				.add(logic("MULTIPLY").input("val1", 3).input("val2", 2))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.build();
	}
	
	@Test
	public void testScenario4() {
		mockLogic("MULTIPLY").when("val1 > val2").returnAs("multiply_result").thenReturn(-1);
		mockLogic("MULTIPLY").when("val2 > val1").thenThrowError(-2, "error occured");
		
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_4").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getError().getCode()).isEqualTo(-2);
		assertThat(ctx.getError().getMessage()).isEqualTo("error occured");
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 4, -1);
		
		assertContext().whenAll("1", "2", "3", "4", "5").areExecuted();
	}

	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2))
				.add(logic("INSERT").input("list", expr("list")).input("val", 4))
				.add(logic("MULTIPLY").input("val1", 3).input("val2", 2))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.add(logic("MULTIPLY").input("val1", 2).input("val2", 3))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.build();
	}
	
	@Test
	public void testScenario5() {
		mockLogic("MULTIPLY").captureAs("MULTIPLY").returnAs("multiply_result").thenReturn(-1);
		
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_5").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(5);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 4, -1);
		assertThat(ctx.getParams().get("list")).asList().doesNotContain(9, 6, 3);
		
		assertContext().whenAll("1", "2", "3", "4").areExecuted();
		assertInputs().capturedAs("MULTIPLY").when("val1 == 3");
		assertInputs().capturedAs("MULTIPLY").when("val2 == 2");
	}

	public static ProcessDefinition scenario5() {
		return createProcess("SIMPLE_SCENARIO_5", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2))
				.add(logic("INSERT").input("list", expr("list")).input("val", 4))
				.add(logic("MULTIPLY").input("val1", 3).input("val2", 3))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.add(logic("MULTIPLY").input("val1", 3).input("val2", 2))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.add(logic("MULTIPLY").input("val1", 3).input("val2", 1))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.build();
	}
	
	@Logic(id = "MULTIPLY", returnAs = "multiply_result")
	public static Integer multiply(@Input("val1") Integer value1, @Input("val2") Integer value2) {
		return value1 * value2;
	}

	@Logic(id = "INSERT")
	public static void insert(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		list.add(value);
	}
}
