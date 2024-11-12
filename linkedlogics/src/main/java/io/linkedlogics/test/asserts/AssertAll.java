package io.linkedlogics.test.asserts;

import java.util.Set;
import java.util.stream.IntStream;

import io.linkedlogics.context.Context;

public class AssertAll {
	private Context context;
	private String[] ids;
	
	public AssertAll(Context context, String... ids) {
		this.context = context ;
		this.ids = ids;
	}
	
	public void areExecuted() {
		Set<String> executed = AssertUtil.getSetByTypes(context, AssertUtil.EXECUTABLE_TYPES);
		
		IntStream.range(0, ids.length).forEach(i -> {
			assert executed.contains(ids[i]) : String.format("%s is NOT executed but expected", ids[i]);
		});
	}
	
	public void areNotExecuted() {
		Set<String> executed = AssertUtil.getSetByTypes(context, AssertUtil.EXECUTABLE_TYPES);
		
		IntStream.range(0, ids.length).forEach(i -> {
			assert !executed.contains(ids[i]) : String.format("%s is executed but NOT expected", ids[i]);
		});
	}
}
