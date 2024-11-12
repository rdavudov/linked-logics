package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.group;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.test.LinkedLogicsTest.assertContext;
import static org.assertj.core.api.Assertions.assertThat;

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
public class ForkJoinProcess1Tests {
	
	@Test
	public void testScenario1() {
		long start = System.currentTimeMillis();
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_1").build());

		TestContextService.blockUntil();
		long finish = System.currentTimeMillis();

		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(finish - start).isGreaterThan(500);
		assertThat(finish - start).isLessThan(2750);
		assertThat(ctx.getParams().containsKey("concat")).isTrue();
		assertThat(ctx.getParams().get("concat")).asString().contains("v1");
		assertThat(ctx.getParams().get("concat")).asString().contains("v2");
		assertThat(ctx.getParams().get("concat")).asString().contains("v3");
		
		assertContext().when("1").onFork().isForked();
		assertContext().when("2").onFork().isForked();
		assertContext().when("3").onFork().isForked();
		
	}

	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("CREATE")
						.input("key", "key1").input("value", "v1").input("delay", 1000L).fork("F1")
						)
				.add(logic("CREATE")
						.input("key", "key2").input("value", "v2").input("delay", 1500L).fork("F2")
						)
				.add(logic("CREATE")
						.input("key", "key3").input("value", "v3").input("delay", 2000L).fork("F3")
						)
				.add(logic("CONCAT")
						.input("val1", expr("key1")).input("val2", expr("key2")).input("val3", expr("key3")).join("F1", "F2", "F3")
						)
				.build();
	}
	
	@Test
	public void testScenario2() {
		long start = System.currentTimeMillis();
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_2").build());
		TestContextService.blockUntil();
		long finish = System.currentTimeMillis();

		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);

		assertThat(finish - start).isGreaterThan(500);
		assertThat(finish - start).isLessThan(2750);
		assertThat(ctx.getParams().containsKey("concat")).isTrue();
		assertThat(ctx.getParams().get("concat")).asString().contains("v1");
		assertThat(ctx.getParams().get("concat")).asString().contains("v3");
		assertThat(ctx.getParams().get("concat_2")).asString().contains("v21");
		assertThat(ctx.getParams().get("concat_2")).asString().contains("v22");
		assertThat(ctx.getParams().get("concat_2")).asString().contains("v23");
		
		assertContext().when("1").onFork().isForked();
		assertContext().when("2").onFork().isForked();
		assertContext().when("3").onFork().isForked();
	}
	
	

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("CREATE").input("key", "key1").input("value", "v1").input("delay", 1000L).fork("F1"))
				.add(group(logic("CREATE").input("key", "key21").input("value", "v21").input("delay", 750L),
						   logic("CREATE").input("key", "key22").input("value", "v22").input("delay", 750L),
						   logic("CREATE").input("key", "key23").input("value", "v23").input("delay", 500L))
						.fork("F2")
						)
				.add(logic("CREATE")
						.input("key", "key3").input("value", "v3").input("delay", 1500L).fork("F3")
						)
				.add(logic("CONCAT")
						.input("val1", expr("key1")).input("val2", "null").input("val3", expr("key3")).join("F1", "F3")
						)
				.add(logic("CONCAT")
						.input("val1", expr("key21")).input("val2", expr("key22")).input("val3", expr("key23")).returnAs("concat_2").join("F2")
						)
				.build();
	}
	
	@Test
	public void testScenario3() {
		long start = System.currentTimeMillis();
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_3").build());
		TestContextService.blockUntil();
		long finish = System.currentTimeMillis();

		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);

		assertThat(finish - start).isLessThan(150);
		assertThat(ctx.getParams().containsKey("concat")).isTrue();
		assertThat(ctx.getParams().get("concat")).asString().contains("key1");
		assertThat(ctx.getParams().get("concat")).asString().contains("key2");
		assertThat(ctx.getParams().get("concat")).asString().contains("key3");
		
		assertContext().when("1").onFork().isForked();
		assertContext().when("2").onFork().isForked();
		assertContext().when("3").onFork().isForked();
	}
	
	

	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("CREATE")
						.input("key", "key1").input("value", "v1").input("delay", 1000L).fork()
						)
				.add(logic("CREATE")
						.input("key", "key2").input("value", "v2").input("delay", 1500L).fork()
						)
				.add(logic("CREATE")
						.input("key", "key3").input("value", "v3").input("delay", 2000L).fork()
						)
				.add(logic("CONCAT")
						.input("val1", "key1").input("val2", "key2").input("val3", "key3")
						)
				.build();
	}
	
	@Logic(id = "CREATE", returnMap = true)
	public static Map<String, String> create(@Input("key") String key, @Input("value") String value, @Input("delay") long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {}
		return Map.of(key, value);
	}
	
	@Logic(id = "CONCAT", returnAs = "concat")
	public static String concat(@Input("val1") String val1, @Input("val2") String val2, @Input("val3") String val3) {
		return val1 + ":" + val2 + ":" + val3;
	}
}
