package io.linkedlogics.model.process;

import static io.linkedlogics.LinkedLogicsBuilder.branch;
import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.error;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.group;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.process.BranchLogicDefinition;
import io.linkedlogics.model.process.GroupLogicDefinition;
import io.linkedlogics.model.process.SingleLogicDefinition;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.local.LocalServiceConfigurer;

public class LogicPositionerTests {
	public static final String PROCESS_A = "proces_a";
	
	public static final String LOGIC_1 = "logic_1";
	public static final String LOGIC_2 = "logic_2";
	public static final String LOGIC_3 = "logic_3";
	
	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerProcess(new ProcessA());
	}
	
	@Test
	public void shouldParseProcess1() {
		Optional<ProcessDefinition> process = ServiceLocator.getInstance().getProcessService().getProcess(PROCESS_A, 0);
		assertThat(process.get().getLogicByPosition("1").get()).isInstanceOf(SingleLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1").get()).getLogicId()).isEqualTo(LOGIC_1);
		assertThat(process.get().getLogicByPosition("2").get()).isInstanceOf(SingleLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("2").get()).getLogicId()).isEqualTo(LOGIC_2);
		assertThat(process.get().getLogicByPosition("3").get()).isInstanceOf(SingleLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("3").get()).getLogicId()).isEqualTo(LOGIC_3);
		assertThat(process.get().getLogicByPosition("4")).isEmpty();
	}
	
	@Test
	public void shouldParseProcess2() {
		Optional<ProcessDefinition> process = ServiceLocator.getInstance().getProcessService().getProcess(PROCESS_A, 1);
		assertThat(process.get().getLogicByPosition("1").get()).isInstanceOf(GroupLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1.1").get()).getLogicId()).isEqualTo(LOGIC_1);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1.2").get()).getLogicId()).isEqualTo(LOGIC_2);
		assertThat(process.get().getLogicByPosition("2").get()).isInstanceOf(GroupLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("2.1").get()).getLogicId()).isEqualTo(LOGIC_2);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("2.2").get()).getLogicId()).isEqualTo(LOGIC_3);
	}
	
	@Test
	public void shouldParseProcess3() {
		Optional<ProcessDefinition> process = ServiceLocator.getInstance().getProcessService().getProcess(PROCESS_A, 2);
		assertThat(process.get().getLogicByPosition("1").get()).isInstanceOf(GroupLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1.1").get()).getLogicId()).isEqualTo(LOGIC_1);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1.2").get()).getLogicId()).isEqualTo(LOGIC_2);
		assertThat(process.get().getLogicByPosition("2.1").get()).isInstanceOf(BranchLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("2.1L").get()).getLogicId()).isEqualTo(LOGIC_2);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("2.1R").get()).getLogicId()).isEqualTo(LOGIC_3);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("2.2").get()).getLogicId()).isEqualTo(LOGIC_3);
	}
	
	@Test
	public void shouldParseProcess4() {
		Optional<ProcessDefinition> process = ServiceLocator.getInstance().getProcessService().getProcess(PROCESS_A, 3);
		assertThat(process.get().getLogicByPosition("1").get()).isInstanceOf(SingleLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1").get()).getLogicId()).isEqualTo(LOGIC_1);
		assertThat(process.get().getLogicByPosition("1Z").get()).isInstanceOf(SingleLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1Z").get()).getLogicId()).isEqualTo(LOGIC_2);
		assertThat(process.get().getLogicByPosition("2").get()).isInstanceOf(SingleLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("2").get()).getLogicId()).isEqualTo(LOGIC_3);
	}
	
	@Test
	public void shouldParseProcess5() {
		Optional<ProcessDefinition> process = ServiceLocator.getInstance().getProcessService().getProcess(PROCESS_A, 4);
		assertThat(process.get().getLogicByPosition("1").get()).isInstanceOf(GroupLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1E").get()).getLogicId()).isEqualTo(LOGIC_3);
	}
	
	public static class ProcessA {
		public ProcessDefinition createProcessA0() {
			return createProcess(PROCESS_A, 0)
					.add(logic(LOGIC_1))
					.add(logic(LOGIC_2))
					.add(logic(LOGIC_3))
					.build();
		}
		
		public ProcessDefinition createProcessA1() {
			return createProcess(PROCESS_A, 1)
					.add(group(
								logic(LOGIC_1), 
								logic(LOGIC_2))
						)
					.add(group(
								logic(LOGIC_2), 
								logic(LOGIC_3))
						)
					.build();
		}
		
		public ProcessDefinition createProcessA2() {
			return createProcess(PROCESS_A, 2)
					.add(group(
								logic(LOGIC_1), 
								logic(LOGIC_2))
						)
					.add(group(
								branch(expr("true"), logic(LOGIC_2), logic(LOGIC_3)), 
								logic(LOGIC_3))
						)
					.build();
		}
		
		public ProcessDefinition createProcessA3() {
			return createProcess(PROCESS_A, 3)
					.add(logic(LOGIC_1).compensate(logic(LOGIC_2)))
					.add(logic(LOGIC_3))
					.build();
		}
		
		public ProcessDefinition createProcessA4() {
			return createProcess(PROCESS_A, 4)
					.add(group(
								logic(LOGIC_1), 
								logic(LOGIC_2))
						.handle(error().withCodes(-1).using(logic(LOGIC_3)))
						)
					.add(group(
								logic(LOGIC_2), 
								logic(LOGIC_3))
						)
					.build();
		}
	}
}
