package io.linkedlogics.config;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.linkedlogics.annotation.Input;
import io.linkedlogics.annotation.Logic;
import io.linkedlogics.context.Context;
import io.linkedlogics.model.parameter.Parameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Main {
	public static void main(String[] args) throws Exception {
//		ProcessFlowHandler.LOG_ENTER = true;
//		ProcessFlowHandler.LOG_EXIT = true;
//		ServiceLocatorTests t = new ServiceLocatorTests();
//		t.setUp();
//		t.resetCounter();
//		t.shouldStartAndStopServiceOnce();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH24:mm:ss.SZ"));
		
		Map<String, Person> persons = new HashMap<>();
		persons.put("p1", new Person("firstname", "lastname", "FREE"));
		persons.put("p2", new Person("firstname", "lastname", "BUSY"));
		persons.put("p3", new Person("firstname", "lastname", "FREE"));
		persons.put("p4", new Person("firstname", "lastname", "BUSY"));
		persons.put("p5", new Person("firstname", "lastname", "BUSY"));
		
		Map<String, Object> map = new HashMap<>() {{ put("persons", persons);}};
		
		Map<String, Object> copyMap = mapper.readValue(mapper.writeValueAsString(map), Map.class);
		Context ctx = new Context();
		ctx.setInput(copyMap);
		
		
		Method method = Arrays.stream(Main.class.getDeclaredMethods()).filter(m -> m.getName().equals("setStates")).findAny().get();
		System.out.println(method);
		
		Parameter[] params = Parameter.initParameters(method);
		Parameter p = params[0];
		Object value = p.getParameterValue(ctx);
		
		Class<? extends Collection> c = (Class<? extends Collection>) p.getType();
		
		Object input = mapper.convertValue(value, mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Person.class));
		method.invoke(null, input);
		
//		if (value instanceof Collection && p.getGenericType() != null) {
//			try {
//				Collection<Object> collection = (Collection<Object>) mapper.convertFrom((Collection) value, p.getType());
//				List<Object> converted = collection.stream().map(e -> mapper.convertFrom(e, p.getGenericType())).collect(Collectors.toList());
//				collection.clear();
//				collection.addAll(converted);
//				
//			} catch (Exception e) {
//				throw new IllegalArgumentException(e);
//			}
//			}
	}
	
	
	@Logic(id = "SET_STATES", version = 0)
	public static void setStates(@Input(value = "persons", returned = true) Map<String, Person> persons) {
		System.out.println("<<<");
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
