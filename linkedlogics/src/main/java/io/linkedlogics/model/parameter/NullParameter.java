package io.linkedlogics.model.parameter;

import io.linkedlogics.context.Context;

public class NullParameter extends Parameter {

	public NullParameter() {
		super(null, null, false, false);
	}
	
	@Override
	protected Object getValue(Context context) {
		return null;
	}
}
