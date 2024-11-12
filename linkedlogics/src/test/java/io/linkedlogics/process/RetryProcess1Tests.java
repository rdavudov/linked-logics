package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.group;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.retry;
import static io.linkedlogics.test.LinkedLogicsTest.assertContext;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
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
public class RetryProcess1Tests {

	private static AtomicInteger retryCounter;

	@BeforeEach
	public void resetCounter() {
		retryCounter = new AtomicInteger();
	}

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_1").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil(12000);
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);

		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(retryCounter.get()).isEqualTo(4);
		assertThat(ctx.getParams().get("list")).asList().hasSize(0);
		assertThat(ctx.getParams().get("list")).asList().contains();
		
		assertContext().when("1").onError().isRetried();
		assertContext().when("1").onError().isRetried(3);
		assertContext().when("2").onError().isNotRetried();
	}


	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 1).retry(3, 1))
				.add(logic("INSERT").input("list", expr("list")).input("val", 2))
				.build();
	}

	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_2").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil(12000);
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().containsKey("list")).isTrue();

		assertThat(retryCounter.get()).isEqualTo(2);
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(1, 2);
		
		assertContext().when("1").onError().isRetried();
		assertContext().when("1").onError().isRetried(1);
		assertContext().when("2").onError().isNotRetried();
	}


	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT_SAFE").input("list", expr("list")).input("val", 1).retry(3, 1))
				.add(logic("INSERT").input("list", expr("list")).input("val", 2))
				.build();
	}

	@Test
	public void testScenario3() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_3").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil(12000);
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(retryCounter.get()).isEqualTo(4);
		assertThat(ctx.getParams().get("list")).asList().hasSize(4);
		assertThat(ctx.getParams().get("list")).asList().contains(2);
		
		assertContext().when("1").onError().isRetried(3);
	}


	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", 2),
						logic("INSERT").input("list", expr("list")).input("val", 1))
						.retry(3, 1)
						)
				.add(logic("INSERT").input("list", expr("list")).input("val", 3))
				.build();
	}
	
	@Test
	public void testScenario4() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_4").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil(12000);
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(retryCounter.get()).isEqualTo(1);
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains(2);
		
		assertContext().when("1").onError().isNotRetried();
	}


	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", 2),
						logic("INSERT").input("list", expr("list")).input("val", 1))
						.retry(retry(3, 1).exclude().codes(-1))
						)
				.add(logic("INSERT").input("list", expr("list")).input("val", 3))
				.build();
	}

	@Logic(id = "INSERT", returnAs = "insert_result")
	public static boolean insert(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		if (value % 2 == 0) {
			list.add(value);
			return true;
		}
		retryCounter.incrementAndGet();
		throw new LogicException(-1, "value is not even", ErrorType.TEMPORARY);
	}

	@Logic(id = "INSERT_SAFE", returnAs = "insert_result")
	public static boolean insertSafe(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		if (value % 2 == 0) {
			list.add(value);
			return true;
		}
		retryCounter.incrementAndGet();
		if (retryCounter.get() == 2) {
			list.add(value % 2);
			return true;
		} else {
			throw new LogicException(-1, "value is not even", ErrorType.TEMPORARY);
		}
	}

	@Logic(id = "REMOVE", returnAs = "remove_result")
	public static boolean remove(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		return list.remove(value);
	}
}
