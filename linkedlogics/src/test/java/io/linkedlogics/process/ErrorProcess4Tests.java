package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.error;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.group;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.verify;
import static io.linkedlogics.LinkedLogicsBuilder.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.annotation.Input;
import io.linkedlogics.annotation.Logic;
import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextBuilder;
import io.linkedlogics.context.Status;
import io.linkedlogics.context.ContextError.ErrorType;
import io.linkedlogics.exception.LogicException;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.test.LinkedLogicsExtension;
import io.linkedlogics.test.service.TestContextService;

@ExtendWith(LinkedLogicsExtension.class)
public class ErrorProcess4Tests {

	
	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_1").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v3", "v6");
	}

	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "e2")
						.handle(error().withCodes(-2).using(logic("INSERT").input("list", expr("list")).input("val", "v2")),
								error().withCodes(-3).using(logic("INSERT").input("list", expr("list")).input("val", "v3")),
								error().withCodes(-4).using(logic("INSERT").input("list", expr("list")).input("val", "v4")),
								error().withCodes(-3).using(logic("INSERT").input("list", expr("list")).input("val", "v5"))))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v6"))
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
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v6");
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "e2")
						.handle(error().withCodes(-2, -3).using(logic("INSERT").input("list", expr("list")).input("val", "v2")),
								error().withCodes(-3).using(logic("INSERT").input("list", expr("list")).input("val", "v3")),
								error().withCodes(-4).using(logic("INSERT").input("list", expr("list")).input("val", "v4")),
								error().withCodes(-3).using(logic("INSERT").input("list", expr("list")).input("val", "v5"))))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v6"))
				.build();
	}

	@Logic(id = "INSERT")
	public static void insert(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		if (value.startsWith("e")) {
			throw new LogicException(-3, "incorrect value", ErrorType.PERMANENT);
		}
		list.add(value);
	}

	@Logic(id = "REMOVE")
	public static void remove(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.remove(value);
	}
}
