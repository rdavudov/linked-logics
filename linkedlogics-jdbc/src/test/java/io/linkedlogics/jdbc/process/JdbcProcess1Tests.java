package io.linkedlogics.jdbc.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.seconds;
import static io.linkedlogics.LinkedLogicsBuilder.var;
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
import io.linkedlogics.jdbc.process.JdbcProcess1Tests.JdbcTestContextService;
import io.linkedlogics.jdbc.service.JdbcContextService;
import io.linkedlogics.jdbc.service.JdbcServiceConfigurer;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.test.LinkedLogicsExtension;
import io.linkedlogics.test.LinkedLogicsRegister;

@ExtendWith(LinkedLogicsExtension.class)
@LinkedLogicsRegister(serviceConfigurerClasses = JdbcServiceConfigurer.class, serviceClasses = JdbcTestContextService.class)
public class JdbcProcess1Tests {
	
	@Test
	public void testScenario1() {
		long start = System.currentTimeMillis();
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_1").build());

		JdbcTestContextService.blockUntil();
		long finish = System.currentTimeMillis();

		Context ctx = JdbcTestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(finish - start).isGreaterThan(500);
		assertThat(finish - start).isLessThan(2750);
		assertThat(ctx.getParams().containsKey("concat")).isTrue();
		assertThat(ctx.getParams().get("concat")).asString().contains("v1");
		assertThat(ctx.getParams().get("concat")).asString().contains("v2");
		assertThat(ctx.getParams().get("concat")).asString().contains("v3");
		
		assertContext(ctx).when("1").onFork().isForked();
		assertContext(ctx).when("2").onFork().isForked();
		assertContext(ctx).when("3").onFork().isForked();
		
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
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_2").params("list", new ArrayList<>()).build());
		JdbcTestContextService.blockUntil();
		long finish = System.currentTimeMillis();
		
		Context ctx = JdbcTestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);

		assertThat(finish - start).isGreaterThan(2500);
		assertThat(finish - start).isLessThan(5000);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v3");
		
		assertContext(ctx).when("1").isExecuted();
		assertContext(ctx).when("2").isExecuted();
		assertContext(ctx).when("2").onDelay().isDelayed();
		assertContext(ctx).when("3").isExecuted();
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", var("list")).input("val", "v1"))
				.add(logic("INSERT").input("list", var("list")).input("val", "v2").delayed(seconds(3)))
				.add(logic("INSERT").input("list", var("list")).input("val", "v3"))
				.build();
	}
	
	@Logic(id = "INSERT", version = 1)
	public static void insert(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.add(value);
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
	
	public static class JdbcTestContextService extends JdbcContextService {

		private static Context currentContext;
		private static Object object = new Object();
		
		public JdbcTestContextService() {
			resetCurrentContext();
		}
		
		@Override
		public void set(Context context) {
			super.set(context);
			
			if (context.getParentId() == null) {
				currentContext = context;
				if (context.isFinished()) {
					unblock();
				}
			}
		}
		
		public static Context getCurrentContext() {
			return currentContext;
		}
		
		public static void resetCurrentContext() {
			currentContext = null;
		}
		
		public static void blockUntil() {
			synchronized (object) {
				try {
					object.wait();
				} catch (InterruptedException e) {}
			}
		}
		
		public static void blockUntil(long timeout) {
			synchronized (object) {
				try {
					object.wait(timeout);
				} catch (InterruptedException e) {}
			}
		}
		
		public static void unblock() {
			synchronized (object) {
				object.notifyAll();
			}
		}
	}

}
