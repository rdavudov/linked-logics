package io.linkedlogics.process;

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
public class SimpleProcess5Tests {

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_3").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(9);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v3", "v4", "v5", "v6");
		
		
	}

	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(process("SIMPLE_SCENARIO_2", 0))
				.add(process("SIMPLE_SCENARIO_1", 0))
				.build();
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3"))
				.add(process("SIMPLE_SCENARIO_1", 0))
				.build();
	}

	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v5"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v6"))
				.build();
	}

	@Logic(id = "INSERT", returnAs = "list")
	public static void insertv2(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.add(value);
	}

	@Logic(id = "REMOVE", returnAs = "list")
	public static List<String> remove(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.remove(value);
		return list;
	}

	@Logic(id = "PRINT")
	public static void print(@Input("list") List<String> list) {
		System.out.println("List = " + list);
	}

}
