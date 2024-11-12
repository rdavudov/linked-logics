package io.linkedlogics.test.asserts;

import io.linkedlogics.context.Context;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AssertWhen {
	private Context context;
	private String id;
	
	public void isExecuted() {
		assert AssertUtil.getMapByTypes(context, AssertUtil.EXECUTABLE_TYPES).getOrDefault(id, Boolean.FALSE) : String.format("%s is NOT executed but expected", id);
	}
	
	public void isNotExecuted() {
		assert !AssertUtil.getMapByTypes(context, AssertUtil.EXECUTABLE_TYPES).getOrDefault(id, Boolean.FALSE) : String.format("%s is executed but NOT expected", id);
	}
	
	public AssertError onError() {
		return new AssertError(context, id);
	}
	
	public AssertFork onFork() {
		return new AssertFork(context, id);
	}
	
	public AssertDelay onDelay() {
		return new AssertDelay(context, id);
	}
	
	public AssertVerify asVerify() {
		return new AssertVerify(context, id);
	}
	
	public AssertLoop asLoop() {
		return new AssertLoop(context, id);
	}
	
	public AssertBranch asBranch() {
		return new AssertBranch(context, id);
	}
}
