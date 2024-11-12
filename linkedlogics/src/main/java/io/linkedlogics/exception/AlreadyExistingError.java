package io.linkedlogics.exception;

import io.linkedlogics.context.ContextError.ErrorType;

@SuppressWarnings("serial")
public class AlreadyExistingError extends LogicException {
	
	public AlreadyExistingError(String id, int version, String type) {
		super(-1, String.format("duplicate %s[%s] v%d ", type, id, version), ErrorType.PERMANENT);
	}
}

