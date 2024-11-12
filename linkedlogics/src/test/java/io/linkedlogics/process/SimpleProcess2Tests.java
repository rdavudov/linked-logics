package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.branch;
import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.group;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
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
public class SimpleProcess2Tests {

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_1").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v4");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").asBranch().isNotSatisfied();
		assertContext().when("3").asBranch().rightBranch().isExecuted();
	}



	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2"))
				.add(branch(when("list.size() < 2"), 
						logic("INSERT").input("list", expr("list")).input("val", "v3"), 
						logic("INSERT").input("list", expr("list")).input("val", "v4")))
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
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").asBranch().isNotSatisfied();
		assertContext().when("3").asBranch().rightBranch().isNotExecuted();
	}



	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2"))
				.add(branch(expr("list.size() < 2"), 
						logic("INSERT").input("list", expr("list")).input("val", "v3")))
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
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v3");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").asBranch().isSatisfied();
		assertContext().when("3").asBranch().leftBranch().isExecuted();
	}



	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2"))
				.add(branch(expr("list.size() > 0"), 
						logic("INSERT").input("list", expr("list")).input("val", "v3"), 
						logic("INSERT").input("list", expr("list")).input("val", "v4")))
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
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v3");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").asBranch().isSatisfied();
		assertContext().when("3").asBranch().leftBranch().isExecuted();
	}



	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2"))
				.add(branch(expr("list.size() > 0"), 
						logic("INSERT").input("list", expr("list")).input("val", "v3")))
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
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v4");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").asBranch().isSatisfied();
		assertContext().when("3").asBranch().leftBranch().asBranch().isNotSatisfied();
	}



	public static ProcessDefinition scenario5() {
		return createProcess("SIMPLE_SCENARIO_5", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2"))
				.add(branch(expr("list.size() > 0"), 
						branch(expr("list.size() > 2"), 
								logic("INSERT").input("list", expr("list")).input("val", "v3"),
								logic("INSERT").input("list", expr("list")).input("val", "v4")),
						logic("INSERT").input("list", expr("list")).input("val", "v5")))
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
		assertThat(ctx.getParams().get("list")).asList().hasSize(6);
		assertThat(ctx.getParams().get("list")).asList().contains("v11", "v12", "v13", "v31", "v32", "v33");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").asBranch().isNotSatisfied();
		assertContext().when("2").asBranch().leftBranch().isNotExecuted();
		assertContext().when("2").asBranch().rightBranch().isExecuted();
	}



	public static ProcessDefinition scenario6() {
		return createProcess("SIMPLE_SCENARIO_6", 0)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v11"),
						logic("INSERT").input("list", expr("list")).input("val", "v12"),
						logic("INSERT").input("list", expr("list")).input("val", "v13")))
				.add(branch(expr("list.size() > 5"), 
						group(logic("INSERT").input("list", expr("list")).input("val", "v21"),
								logic("INSERT").input("list", expr("list")).input("val", "v22"),
								logic("INSERT").input("list", expr("list")).input("val", "v23")),
						group(logic("INSERT").input("list", expr("list")).input("val", "v31"),
								logic("INSERT").input("list", expr("list")).input("val", "v32"),
								logic("INSERT").input("list", expr("list")).input("val", "v33"))))
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
		assertThat(ctx.getParams().get("list")).asList().contains("v11", "v12", "v13");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").asBranch().isSatisfied();
		assertContext().when("3").asBranch().leftBranch().isNotExecuted();
		assertContext().when("3").asBranch().rightBranch().isNotExecuted();
	}



	public static ProcessDefinition scenario7() {
		return createProcess("SIMPLE_SCENARIO_7", 0)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v11"),
						logic("INSERT").input("list", expr("list")).input("val", "v12"),
						logic("INSERT").input("list", expr("list")).input("val", "v13")))
				.add(branch(expr("list.size() < 5"), 
						group(logic("INSERT").input("list", expr("list")).input("val", "v21"),
								logic("INSERT").input("list", expr("list")).input("val", "v22"),
								logic("INSERT").input("list", expr("list")).input("val", "v23")).disabled(),
						group(logic("INSERT").input("list", expr("list")).input("val", "v31"),
								logic("INSERT").input("list", expr("list")).input("val", "v32"),
								logic("INSERT").input("list", expr("list")).input("val", "v33"))))
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
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v2");
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isExecuted();
		assertContext().when("3").asBranch().isSatisfied();
	}



	public static ProcessDefinition scenario8() {
		return createProcess("SIMPLE_SCENARIO_8", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1"))
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2"))
				.add(branch(expr("list.size() > 0"), 
						logic("REMOVE").input("list", expr("list")).input("val", "v1")))
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
