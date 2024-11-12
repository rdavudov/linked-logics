package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.branch;
import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.group;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.test.LinkedLogicsTest.assertContext;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class SimpleProcess3Tests {

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_1").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 4);
		
		assertContext().whenAll("1", "2", "3", "4").areExecuted();
	}



	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 1))
				.add(logic("INSERT").input("list", expr("list")).input("val", 2))
				.add(logic("INSERT").input("list", expr("list")).input("val", 3))
				.add(logic("INSERT").input("list", expr("list")).input("val", 4))
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
		assertThat(ctx.getParams().get("list")).asList().contains(2, 4, 6);
		
		assertContext().whenAll("1", "2", "3", "4", "5").areExecuted();
	}



	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 1))
				.add(logic("INSERT").input("list", expr("list")).input("val", 2))
				.add(logic("MULTIPLY").input("val1", 3).input("val2", 2))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.add(logic("INSERT").input("list", expr("list")).input("val", 4))
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
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 8);
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").asBranch().isNotSatisfied();
		assertContext().when("3").isExecuted();
		assertContext().when("4").asBranch().isSatisfied();
	}



	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 1))
				.add(branch(expr("insert_result"), logic("INSERT").input("list", expr("list")), logic("INSERT").inputs("list", expr("list"), "val", "5")).inputs("val", 4))
				.add(logic("INSERT").input("list", expr("list")).input("val", 2))
				.add(branch(expr("insert_result"), logic("INSERT").input("list", expr("list")).input("val", 8)))
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
		assertThat(ctx.getParams().get("list")).asList().contains(2, 8, 16);
		
		assertContext().whenAll("1", "2", "3", "4").areExecuted();
	}


	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2))
				.add(logic("MULTIPLY_ADD").input("val1", 4).input("val2", 4))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("add_result")))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
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
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(8, 16);
		
		assertContext().whenAll("1", "2", "3").areExecuted();
	}


	public static ProcessDefinition scenario5() {
		return createProcess("SIMPLE_SCENARIO_5", 0)
				.add(group(logic("MULTIPLY"),
						logic("ADD"))
						.input("val1", 4).input("val2", 4)
						)
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("add_result")))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.build();
	}

	@Logic(id = "MULTIPLY", returnAs = "multiply_result")
	public static Integer multiply(@Input("val1") Integer value1, @Input("val2") Integer value2) {
		return value1 * value2;
	}

	@Logic(id = "ADD", returnAs = "add_result")
	public static Integer add(@Input("val1") Integer value1, @Input("val2") Integer value2) {
		return value1 + value2;
	}

	@Logic(id = "MULTIPLY_ADD", returnMap = true)
	public static Map<String, Integer> multiplyAndAdd(@Input("val1") Integer value1, @Input("val2") Integer value2) {
		return Map.of("multiply_result", value1 * value2, "add_result", value1 + value2);
	}

	@Logic(id = "INSERT", returnAs = "insert_result")
	public static boolean insert(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		if (value % 2 == 0) {
			list.add(value);
			return true;
		}
		return false;
	}

	@Logic(id = "REMOVE", returnAs = "remove_result")
	public static boolean remove(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		return list.remove(value);
	}

	@Logic(id = "PRINT")
	public static void print(@Input("list") List<String> list) {
		System.out.println("List = " + list);
	}
}
