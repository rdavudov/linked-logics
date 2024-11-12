package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.verify;
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
public class CompensateProcess1Tests {

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
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1"))
						)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))
						)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))
						)
				.build();
	}

	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_2").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v4");
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isCompensated();
		assertContext().when("2").isExecuted();
		assertContext().when("2").onError().isCompensated();
		assertContext().when("3").isExecuted();
		assertContext().when("3").onError().isCompensated();
		assertContext().when("4").asVerify().isNotVerified();
		assertContext().when("5").isExecuted();
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1"))
						)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))
						)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))
						)
				.add(verify(expr("list.size() < 1")))
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v4").forced())
				.build();
	}

	@Test
	public void testScenario3() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_3").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v4");
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isCompensated();
		assertContext().when("2").isExecuted();
		assertContext().when("2").onError().isCompensated();
		assertContext().when("3").asVerify().isNotVerified();
		assertContext().when("4").isNotExecuted();
		assertContext().when("4").onError().isNotCompensated();
		assertContext().when("5").isExecuted();
	}

	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1"))
						)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))
						)
				.add(verify(expr("list.size() < 1")))
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))
						)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v4").forced())
				.build();
	}

	@Test
	public void testScenario4() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_4").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v4");
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isCompensated();
		assertContext().when("2").asVerify().isNotVerified();
		assertContext().when("3").isNotExecuted();
		assertContext().when("3").onError().isNotCompensated();
		assertContext().when("4").isNotExecuted();
		assertContext().when("4").onError().isNotCompensated();
		assertContext().when("5").isExecuted();
	}

	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1"))
						)
				.add(verify(expr("list.size() < 1")))
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))
						)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))
						)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v4").forced())
				.build();
	}

	@Test
	public void testScenario5() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_5").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v1");
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().compensation().isNotExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("2").onError().isCompensated();
		assertContext().when("3").isExecuted();
		assertContext().when("3").onError().isCompensated();
		assertContext().when("4").asVerify().isNotVerified();
	}

	public static ProcessDefinition scenario5() {
		return createProcess("SIMPLE_SCENARIO_5", 0)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").disabled())
						)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))
						)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))
						)
				.add(verify(expr("list.size() < 1")))
				.build();
	}

	@Test
	public void testScenario6() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_6").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v2", "v4");
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isCompensated();
		assertContext().when("2").isExecuted();
		assertContext().when("2").onError().compensation().isNotExecuted();
		assertContext().when("3").asVerify().isNotVerified();
		assertContext().when("4").isNotExecuted();
		assertContext().when("5").isExecuted();
	}

	public static ProcessDefinition scenario6() {
		return createProcess("SIMPLE_SCENARIO_6", 0)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1"))
						)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").disabled())
						)
				.add(verify(expr("list.size() < 1")))
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))
						)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v4").forced())
				.build();
	}

	@Logic(id = "INSERT")
	public static void insert(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.add(value);
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
