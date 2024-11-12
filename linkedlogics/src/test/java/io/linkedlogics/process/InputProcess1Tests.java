package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ExtendWith(LinkedLogicsExtension.class)
public class InputProcess1Tests {

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_1").params("person", new Person("firstname", "lastname", "FREE")).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("person")).isTrue();
		assertThat(((Map<String, Object>) ctx.getParams().get("person")).get("state")).isEqualTo("BUSY");
		assertThat(ctx.getParams().get("fullname")).isEqualTo("firstname lastname");
	}


	public ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("GET_FULLNAME").input("person", expr("person")))
				.add(logic("SET_STATE").input("person", expr("person")))
				.build();
	}
	
	@Test
	public void testScenario2() {
		List<Person> persons = new ArrayList<>();
		persons.add(new Person("firstname", "lastname", "FREE"));
		persons.add(new Person("firstname", "lastname", "BUSY"));
		persons.add(new Person("firstname", "lastname", "FREE"));
		persons.add(new Person("firstname", "lastname", "BUSY"));
		persons.add(new Person("firstname", "lastname", "BUSY"));
		
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_2").params("persons", persons).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("busy_person_count")).isTrue();
		assertThat(ctx.getParams().get("busy_person_count")).isEqualTo(3);
	}


	public ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("COUNT_BUSY_STATES").input("persons", expr("persons")))
				.build();
	}
	
	@Test
	public void testScenario3() {
		List<Person> persons = new ArrayList<>();
		persons.add(new Person("firstname1", "lastname1", "FREE"));
		persons.add(new Person("firstname2", "lastname2", "FREE"));
		persons.add(new Person("firstname3", "lastname3", "FREE"));
		persons.add(new Person("firstname1", "lastname1", "FREE"));
		persons.add(new Person("firstname3", "lastname3", "FREE"));
		
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_3").params("persons", persons).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("unique_person_count")).isTrue();
		assertThat(ctx.getParams().get("unique_person_count")).isEqualTo(3);
	}


	public ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("COUNT_UNIQUE").input("persons", expr("persons")))
				.build();
	}
	
	@Test
	public void testScenario4() {
		Map<String, Person> persons = new HashMap<>();
		persons.put("firstname1", new Person("firstname1", "lastname1", "FREE"));
		persons.put("firstname2", new Person("firstname2", "lastname2", "FREE"));
		persons.put("firstname3", new Person("firstname3", "lastname3", "FREE"));
		persons.put("firstname4", new Person("firstname4", "lastname4", "FREE"));
		persons.put("firstname5", new Person("firstname5", "lastname5", "FREE"));
		
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_4").params("persons", persons).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("check_by_firstname")).isTrue();
		assertThat(ctx.getParams().get("check_by_firstname")).isEqualTo(true);
	}


	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(logic("CHECK_BY_FIRSTNAME").input("persons", expr("persons")).input("firstname", "firstname1"))
				.build();
	}
	
	@Test
	public void testScenario5() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_5").params("firstname", "firstname", "lastname", "lastname", "state", "FREE").build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().get("fullname")).isEqualTo("firstname lastname");
	}


	public ProcessDefinition scenario5() {
		return createProcess("SIMPLE_SCENARIO_5", 0)
				.add(logic("GET_PERSON").inputs("firstname", "firstname", "lastname", "lastname", "state", "FREE", "unknown", "unknown"))
				.build();
	}
	
	@Test
	public void testScenario6() {
		String contextId = LinkedLogics.start(ContextBuilder.newContext("SIMPLE_SCENARIO_6").params("firstname", "firstname", "lastname", "lastname", "state", "FREE").build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().get("fullname")).isEqualTo("firstname lastname");
	}


	public ProcessDefinition scenario6() {
		return createProcess("SIMPLE_SCENARIO_6", 0)
				.add(logic("GET_PERSON_MAP").inputs("firstname", "firstname", "lastname", "lastname", "state", "FREE", "unknown", "unknown"))
				.build();
	}

	@Logic(id = "GET_FULLNAME", returnAs = "fullname")
	public String getFullname(@Input(value = "person") Person person) {
		return person.getFirstname() + " " + person.getLastname();
	}

	@Logic(id = "SET_STATE", version = 0)
	public void setState(@Input(value = "person", returned = true) Person person) {
		person.setState("BUSY");
	}

	@Logic(id = "COUNT_BUSY_STATES", returnAs = "busy_person_count")
	public long countBusyStates(@Input(value = "persons") List<Person> persons) {
		return persons.stream().filter(p -> p.getState().equals("BUSY")).count();
	}

	@Logic(id = "COUNT_UNIQUE", returnAs = "unique_person_count")
	public long countUnique(@Input(value = "persons") Set<Person> persons) {
		return persons.size();
	}

	@Logic(id = "CHECK_BY_FIRSTNAME", returnAs = "check_by_firstname")
	public boolean checkByFirstname(@Input(value = "persons") Map<String, Person> persons, @Input("firstname") String firstname) {
		return persons.containsKey(firstname);
	}
	
	@Logic(id = "GET_PERSON", returnAs = "fullname")
	public String getPerson(@Input Person person) {
		return person.getFirstname() + " " + person.getLastname();
	}
	
	@Logic(id = "GET_PERSON_MAP", returnAs = "fullname")
	public String getPerson(@Input Map<String, Object> person) {
		return person.get("firstname") + " " + person.get("lastname");
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	private static class Person {
		private String firstname;
		private String lastname;
		private String state;
	}
}
