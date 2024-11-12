package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
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
import io.linkedlogics.context.ContextError.ErrorType;
import io.linkedlogics.context.Status;
import io.linkedlogics.exception.LogicException;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.test.LinkedLogicsExtension;
import io.linkedlogics.test.service.TestContextService;

@ExtendWith(LinkedLogicsExtension.class)
public class AsyncProcess1Tests {

	@Test
	public void testScenario1() {
		long start = System.currentTimeMillis();
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_1").param("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		long finish = System.currentTimeMillis();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);

		assertThat(finish - start).isGreaterThan(500);
		assertThat(finish - start).isLessThan(2000);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 4, 6);
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").isExecuted();
		assertContext().when("4").isExecuted();
		assertContext().when("5").isExecuted();
	}

	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 1))
				.add(logic("INSERT").input("list", expr("list")).input("val", 2))
				.add(logic("MULTIPLY_ASYNC").input("val1", 3).input("val2", 2).input("delay", 500L))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.add(logic("INSERT").input("list", expr("list")).input("val", 4))
				.build();
	}

	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_2").param("list", new ArrayList<>()).build());
		TestContextService.blockUntil(10000);
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		
		assertThat(ctx.getError()).isNotNull();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains(2);
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").isExecuted();
		assertContext().when("4").isNotExecuted();
		assertContext().when("5").isNotExecuted();
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 1))
				.add(logic("INSERT").input("list", expr("list")).input("val", 2))
				.add(logic("MULTIPLY_ASYNC").input("val1", 3).input("val2", 2).input("delay", 6000L))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.add(logic("INSERT").input("list", expr("list")).input("val", 4))
				.build();
	}

	@Test
	public void testScenario3() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_3").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil(10000);
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		
		assertThat(ctx.getError()).isNotNull();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 4);
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").isExecuted();
		assertContext().when("4").isNotExecuted();
		assertContext().when("5").isExecuted();
		assertContext().when("6").isNotExecuted();
	}

	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 1))
				.add(logic("INSERT").input("list", expr("list")).input("val", 2))
				.add(logic("MULTIPLY_ERROR").input("val1", 3).input("val2", 2).input("delay", 500L))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.add(logic("INSERT").input("list", expr("list")).input("val", 4).forced())
				.add(logic("INSERT").input("list", expr("list")).input("val", 6))
				.build();
	}

	@Logic(id = "MULTIPLY", returnAs = "multiply_result")
	public static Integer multiply(@Input("val1") Integer value1, @Input("val2") Integer value2) {
		return value1 * value2;
	}

	@Logic(id = "MULTIPLY_ASYNC", returnAsync = true, returnAs = "multiply_result")
	public static void multiply(@Input("val1") Integer value1, @Input("val2") Integer value2, @Input("delay") Long delay) {
		String contextId = LinkedLogics.getContextId();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {}
				LinkedLogics.asyncCallback(contextId, value1 * value2);
			}
		}).start();
	}

	@Logic(id = "MULTIPLY_ERROR", returnAsync = true, returnAs = "multiply_result")
	public static void multiplyError(@Input("val1") Integer value1, @Input("val2") Integer value2, @Input("delay") Long delay) {
		String contextId = LinkedLogics.getContextId();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {}
				LinkedLogics.asyncCallback(contextId, new LogicException(-1, "multiplication error", ErrorType.PERMANENT));
			}
		}).start();
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

}
