package io.linkedlogics.test.asserts;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow.Type;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AssertLoop {
	private Context context;
	private String id;
	
	public void isIterated(int times) {
		int retries = AssertUtil.countResults(context, Type.LOOP, id, true);
		assert retries == times : String.format("Loop on %s iterated %d times but expected was %d times", id, retries, times);
	}
	
	public void isIteratedAtMost(int times) {
		int retries = AssertUtil.countResults(context, Type.LOOP, id, true);
		assert retries <= times : String.format("Loop on %s iterated %d times but expected was at most %d", id, retries, times);
	}
	
	public void isIteratedAtLeast(int times) {
		int retries = AssertUtil.countResults(context, Type.LOOP, id, true);
		assert retries >= times : String.format("Loop on %s iterated %d times but expected was at least %d ", id, retries, times);
	}
}
