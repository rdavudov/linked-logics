package io.linkedlogics.test.asserts;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AssertDelay {
	private Context context;
	private String id;
	
	public void isDelayed() {
		assert AssertUtil.getResultOrDefault(context, ContextFlow.Type.DELAY, id, Boolean.FALSE) : String.format("%s is NOT delayed but expected", id);
	}
	
	public void isNotDelayed() {
		assert !AssertUtil.getResultOrDefault(context, ContextFlow.Type.DELAY, id, Boolean.FALSE) : String.format("%s is delayed but NOT expected", id);
	}
}