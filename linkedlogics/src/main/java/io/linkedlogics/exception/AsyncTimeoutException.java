package io.linkedlogics.exception;

import io.linkedlogics.context.ContextError.ErrorType;

@SuppressWarnings("serial")
public class AsyncTimeoutException extends LogicException {
	
	public AsyncTimeoutException(String logicId, int version) {
		super(-1, String.format("async logic logic[%s] v%d timed out", logicId, version), ErrorType.TEMPORARY);
	}
}
