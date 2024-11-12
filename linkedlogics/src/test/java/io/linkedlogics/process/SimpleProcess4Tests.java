package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.branch;
import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.process;
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
public class SimpleProcess4Tests {

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_1") .params("list", new ArrayList<>(), "val", 3).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains(6);
	}

	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_1").params("list", new ArrayList<>(), "val", 7).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains(28);
	}
	

	public static ProcessDefinition scenario2v0() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("MULTIPLY").input("val1", expr("val")).input("val2", 2))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.build();
	}


	public static ProcessDefinition scenario2v1() {
		return createProcess("SIMPLE_SCENARIO_2", 1)
				.add(logic("MULTIPLY").input("val1", expr("val")).input("val2", 4))
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")))
				.build();
	}


	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(branch(expr("val > 5"), 
						process("SIMPLE_SCENARIO_2", 1),
						process("SIMPLE_SCENARIO_2", 0))
					)
				.build();
	}

	@Logic(id = "MULTIPLY", returnAs = "multiply_result")
	public static Integer multiply(@Input("val1") Integer value1, @Input("val2") Integer value2) {
		return value1 * value2;
	}

	@Logic(id = "INSERT", returnAs = "insert_result")
	public static boolean insert(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		list.add(value);
		return true;
	}
}
