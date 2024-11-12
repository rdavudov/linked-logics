package io.linkedlogics.test.asserts;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.context.ContextFlow.Type;

public class AssertUtil {
	
	public static final Set<Type> EXECUTABLE_TYPES = Set.of(ContextFlow.Type.LOGIC, 
			ContextFlow.Type.SCRIPT,
			ContextFlow.Type.GROUP,
			ContextFlow.Type.LOG,
			ContextFlow.Type.FAIL,
			ContextFlow.Type.EXIT,
			ContextFlow.Type.JUMP,
			ContextFlow.Type.BRANCH,
			ContextFlow.Type.VERIFY,
			ContextFlow.Type.LOOP,
			ContextFlow.Type.SAVEPOINT);

	public static Boolean getResult(Context context, Type type, String id) {
		return getMapByType(context, type).get(id);
	}
	
	public static Boolean getResultOrDefault(Context context, Type type, String id, Boolean defaultResult) {
		return getMapByType(context, type).getOrDefault(id, defaultResult);
	}
	
	public static Integer countResults(Context context, Type type, String id, Boolean result) {
		return getListByType(context, type, result).stream().filter(s -> s.equals(id)).collect(Collectors.toList()).size();
	}
	
	public static Map<String, Boolean> getMapByType(Context context, Type type) {
		return context.getExecList()
			.stream()
			.filter(s -> s.getType().equals(type))
			.collect(Collectors.toMap(s -> s.getPosition(), s -> s.getResult(), (a, b) -> b));
	}
	
	public static Map<String, Boolean> getMapByTypes(Context context, Set<Type> types) {
		Set<String> typeNames = types.stream().map(t -> t.name()).collect(Collectors.toSet());
		
		return context.getExecList()
			.stream()
			.filter(s -> typeNames.contains(s.getType().name()))
			.collect(Collectors.toMap(s -> s.getPosition(), s -> s.getResult(), (a, b) -> b));
	}
	
	public static Map<String, Boolean> getMap(Context context) {
		return context.getExecList()
			.stream()
			.collect(Collectors.toMap(s -> s.getPosition(), s -> s.getResult(), (a, b) -> b));
	}
	
	public static Set<String> getSet(Context context) {
		return context.getExecList()
			.stream()
			.map(s -> s.getPosition())
			.collect(Collectors.toSet());
	}
	
	public static Set<String> getSetByType(Context context, Type type) {
		return context.getExecList()
			.stream()
			.filter(s -> s.getType().equals(type))
			.map(s -> s.getPosition())
			.collect(Collectors.toSet());
	}
	
	public static Set<String> getSetByTypes(Context context, Set<Type> types) {
		Set<String> typeNames = types.stream().map(t -> t.name()).collect(Collectors.toSet());
		
		return context.getExecList()
			.stream()
			.filter(s -> typeNames.contains(s.getType().name()))
			.map(s -> s.getPosition())
			.collect(Collectors.toSet());
	}
	
	public static List<String> getListByType(Context context, Type type, Boolean result) {
		return context.getExecList()
			.stream()
			.filter(s -> s.getType().equals(type))
			.filter(s -> s.getResult() == result)
			.map(s -> s.getPosition())
			.collect(Collectors.toList());
	}
}
