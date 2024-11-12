package io.linkedlogics.test.asserts;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AssertFork {
	private Context context;
	private String id;
	
	public void isForked() {
		assert AssertUtil.getResultOrDefault(context, ContextFlow.Type.FORK, id, Boolean.FALSE) : String.format("%s is NOT forked but expected", id);
	}
	
	public void isNotForked() {
		assert !AssertUtil.getResultOrDefault(context, ContextFlow.Type.FORK, id, Boolean.FALSE) : String.format("%s is forked but NOT expected", id);
	}
}