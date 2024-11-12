package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.error;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.group;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.savepoint;
import static io.linkedlogics.LinkedLogicsBuilder.verify;
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
public class ErrorProcess1Tests {

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_1").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v1");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("2.1").isExecuted();
		assertContext().when("2.2").isExecuted();
		assertContext().when("2.3").asVerify().isNotVerified();
		assertContext().when("2.1").onError().isCompensated();
		assertContext().when("2.2").onError().isCompensated();
		assertContext().when("3").isNotExecuted();
	}

	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))	
						,
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))
						,
						verify(when("false")).elseFailWithCode(-100).andMessage("failure"))
						)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4"))
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
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v4");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("2.1").isExecuted();
		assertContext().when("2.2").isExecuted();
		assertContext().when("2.3").asVerify().isNotVerified();
		assertContext().when("2.1").onError().isCompensated();
		assertContext().when("2.2").onError().isCompensated();
		assertContext().when("3").isExecuted();
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))	
						,
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))
						,
						verify(expr("false")).elseFailWithCode(-100).andMessage("failure"))
						)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4").forced())
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
		assertThat(ctx.getParams().get("list")).asList().hasSize(0);
		assertThat(ctx.getParams().get("list")).asList().contains();
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isCompensated();
		assertContext().when("2").isExecuted();
		assertContext().when("2.1").isExecuted();
		assertContext().when("2.2").isExecuted();
		assertContext().when("2.3").asVerify().isNotVerified();
		assertContext().when("2.1").onError().isCompensated();
		assertContext().when("2.2").onError().isCompensated();
		assertContext().when("3").isNotExecuted();
	}

	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1"))
						)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))	
						,
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))
						,
						verify(expr("false")).elseFailWithCode(-100).andMessage("failure"))
						)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4"))
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
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v4");
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isNotCompensated();
		assertContext().when("2").isExecuted();
		assertContext().when("2.1").isExecuted();
		assertContext().when("2.2").isExecuted();
		assertContext().when("2.3").asVerify().isNotVerified();
		assertContext().when("2").onError().isHandled();
		assertContext().when("2.1").onError().isCompensated();
		assertContext().when("2.2").onError().isCompensated();
		assertContext().when("3").isExecuted();
	}

	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1"))
						)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))	
						,
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))
						,
						verify(expr("false")).elseFailWithCode(-100).andMessage("failure")).handle(error().withCodes(-100))
						)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4"))
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
		assertThat(ctx.getParams().get("list")).asList().hasSize(0);
		assertThat(ctx.getParams().get("list")).asList().contains();
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isCompensated();
		assertContext().when("2").isExecuted();
		assertContext().when("2.1").isExecuted();
		assertContext().when("2.2").isExecuted();
		assertContext().when("2.3").asVerify().isNotVerified();
		assertContext().when("2").onError().isNotHandled();
		assertContext().when("2.1").onError().isCompensated();
		assertContext().when("2.2").onError().isCompensated();
		assertContext().when("3").isNotExecuted();
	}

	public static ProcessDefinition scenario5() {
		return createProcess("SIMPLE_SCENARIO_5", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1"))
						)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))	
						,
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))
						,
						verify(expr("false")).elseFailWithCode(-100).andMessage("failure"))
						.handle(error().withCodes(-200))
						)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4"))
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
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v4");
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isNotCompensated();
		assertContext().when("2").isExecuted();
		assertContext().when("2.1").isExecuted();
		assertContext().when("2.2").isExecuted();
		assertContext().when("2.3").asVerify().isNotVerified();
		assertContext().when("2").onError().isHandled();
		assertContext().when("2.1").onError().isCompensated();
		assertContext().when("2.2").onError().isCompensated();
		assertContext().when("3").isExecuted();
	}

	public static ProcessDefinition scenario6() {
		return createProcess("SIMPLE_SCENARIO_6", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1"))
						)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))	
						,
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))
						,
						verify(expr("false")).elseFailWithCode(-100).andMessage("failure"))
						.handle(error().withCodes(-200).orMessages("fail"))
						)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4"))
				.build();
	}

	@Test
	public void testScenario7() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_7").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(0);
		assertThat(ctx.getParams().get("list")).asList().contains();
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isCompensated();
		assertContext().when("2").isExecuted();
		assertContext().when("2.1").isExecuted();
		assertContext().when("2.2").isExecuted();
		assertContext().when("2.3").asVerify().isNotVerified();
		assertContext().when("2").onError().isNotHandled();
		assertContext().when("2.1").onError().isCompensated();
		assertContext().when("2.2").onError().isCompensated();
		assertContext().when("3").isNotExecuted();
	}

	public static ProcessDefinition scenario7() {
		return createProcess("SIMPLE_SCENARIO_7", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1"))
						)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))	
						,
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))
						,
						verify(expr("false")).elseFailWithCode(-100).andMessage("failure"))
						.handle(error().withCodes(-200).orMessages("error"))
						)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4"))
				.build();
	}

	@Test
	public void testScenario8() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_8").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v6");
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isNotCompensated();
		assertContext().when("2").isExecuted();
		assertContext().when("2").onError().isHandled();
		assertContext().when("3").isExecuted();
	}

	public static ProcessDefinition scenario8() {
		return createProcess("SIMPLE_SCENARIO_8", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1"))
						)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))	
						,
						group(logic("INSERT").input("list", expr("list")).input("val", "v3")
								.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))	
								,
								logic("INSERT").input("list", expr("list")).input("val", "v4")
								.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v4"))
								,
								verify(expr("false")).elseFailWithCode(-200).andMessage("failure"))
						.handle(error().withCodes(-100).orMessages("error"))
						,
						logic("INSERT").input("list", expr("list")).input("val", "v5")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v5"))
						
						)
						.handle(error().withCodes(-200).orMessages("error"))
						)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v6"))
				.build();
	}

	@Test
	public void testScenario9() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_9").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v6", "v7");
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isNotCompensated();
		assertContext().when("2").isExecuted();
		assertContext().when("2").onError().isHandled();
		assertContext().when("3").isExecuted();
	}

	public static ProcessDefinition scenario9() {
		return createProcess("SIMPLE_SCENARIO_9", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1"))
						)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))	
						,
						group(logic("INSERT").input("list", expr("list")).input("val", "v3")
								.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))	
								,
								logic("INSERT").input("list", expr("list")).input("val", "v4")
								.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v4"))
								,
								verify(expr("false")).elseFailWithCode(-200).andMessage("failure"))
						.handle(error().withCodes(-100).orMessages("error"))
						,
						logic("INSERT").input("list", expr("list")).input("val", "v5")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v5"))
						
						)
						.handle(error().withCodes(-200).orMessages("error").using(logic("INSERT").input("list", expr("list")).input("val", "v6")))
						)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v7"))
				.build();
	}

	@Test
	public void testScenario10() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_10").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v7");
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isNotCompensated();
		assertContext().when("2").isExecuted();
		assertContext().when("2").onError().isHandled();
		assertContext().when("3").isExecuted();
	}

	public static ProcessDefinition scenario10() {
		return createProcess("SIMPLE_SCENARIO_10", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1"))
						)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))	
						,
						group(logic("INSERT").input("list", expr("list")).input("val", "v3")
								.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))	
								,
								logic("INSERT").input("list", expr("list")).input("val", "v4")
								.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v4"))
								,
								verify(expr("false")).elseFailWithCode(-200).andMessage("failure"))
						.handle(error().withCodes(-100).orMessages("error"))
						,
						logic("INSERT").input("list", expr("list")).input("val", "v5")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v5"))
						
						)
						.handle(error().withCodes(-200).orMessages("error").using(logic("INSERT").input("list", expr("list")).input("val", "v6").disabled()))
						)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v7"))
				.build();
	}
	
	@Test
	public void testScenario11() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_11").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v1");
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isNotCompensated();
		assertContext().when("2").isExecuted();
		assertContext().when("3").onError().isNotHandled();
		assertContext().when("4").isNotExecuted();
	}

	public static ProcessDefinition scenario11() {
		return createProcess("SIMPLE_SCENARIO_11", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1"))
						)
				.add(savepoint())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2"))	
						,
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3"))
						,
						verify(when("false")).elseFailWithCode(-100).andMessage("failure"))
						.handle(error().withCodes(-200))
						)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4"))
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
}
