package io.linkedlogics.test.asserts;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.model.process.helper.LogicPositioner;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AssertBranch {
	private Context context;
	private String id;
	
	public void isSatisfied() {
		Boolean result = AssertUtil.getResult(context, ContextFlow.Type.BRANCH, id);
		assert result != null : String.format("Branch %s is NOT executed but expected", id);
		assert result : String.format("Branch %s is NOT satisfied but expected", id);
	}
	
	public void isNotSatisfied() {
		Boolean result = AssertUtil.getResult(context, ContextFlow.Type.BRANCH, id);
		assert result != null : String.format("Branch %s is NOT executed but expected", id);
		assert !result : String.format("Branch %s is satisfied but NOT expected", id);
	}
	
	public AssertWhen leftBranch() {
		return new AssertWhen(context, id + LogicPositioner.BRANCH_LEFT);
	}
	
	public AssertWhen rightBranch() {
		return new AssertWhen(context, id + LogicPositioner.BRANCH_RIGHT);
	}
}
