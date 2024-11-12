package io.linkedlogics.exception;

import io.linkedlogics.context.ContextError.ErrorType;

@SuppressWarnings("serial")
public class MissingInputParameterError extends LogicException {
	
	public MissingInputParameterError(String paramName) {
		super(-1, String.format("missing input parameter [%s] ", paramName), ErrorType.PERMANENT);
	}
}

