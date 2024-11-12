package io.linkedlogics.test.asserts;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AssertVerify {
	private Context context;
	private String id;
	
	public void isVerified() {
		Boolean result = AssertUtil.getResult(context, ContextFlow.Type.VERIFY, id);
		assert result != null : String.format("Verify %s is NOT executed but expected", id);
		assert result : String.format("Verify %s is NOT verified but expected", id);
	}
	
	public void isNotVerified() {
		Boolean result = AssertUtil.getResult(context, ContextFlow.Type.VERIFY, id);
		assert result != null : String.format("Verify %s is NOT executed but expected", id);
		assert !result : String.format("Verify %s is verified but NOT expected", id);
	}
}
