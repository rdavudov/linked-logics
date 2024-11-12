package io.linkedlogics.model.parameter;

import io.linkedlogics.context.Context;

public class InputParameter extends Parameter {

	public InputParameter(String name, Class<?> type, boolean required, boolean returned) {
		super(name, type, required, returned);
	}
	
	@Override
	protected Object getValue(Context context) {
		if (name == null || name.length() == 0) {
			return context.getInput();
		}
		return context.getInput().get(name);
	}
}
