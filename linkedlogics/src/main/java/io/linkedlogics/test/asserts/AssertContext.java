package io.linkedlogics.test.asserts;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.linkedlogics.context.Context;
import io.linkedlogics.test.service.TestContextService;

public class AssertContext {
	private Context context;
	private String id = "";
	
	public AssertContext(Context context) {
		this.context = context;
	}
	
	public AssertContext() {
		this.context = TestContextService.getCurrentContext();
	}
	
	public AssertContext(String id) {
		this.context = TestContextService.getCurrentContext();
		this.id = id;
	}
	
	public AssertWhen when(String id) {
		return new AssertWhen(context, this.id + id);
	}
	
	public AssertAll whenAll(String... ids) {
		List<String> idList = Arrays.stream(ids).map(i -> this.id + i).collect(Collectors.toList());
		String[] newIds = new String[ids.length];
		idList.toArray(newIds);
		return new AssertAll(context, newIds);
	}
}
