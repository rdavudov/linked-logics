package io.linkedlogics.exception;

import io.linkedlogics.context.ContextError.ErrorType;

@SuppressWarnings("serial")
public class LogicTimeoutException extends LogicException {
	
	public LogicTimeoutException(String logicId) {
		super(-1, String.format("logic logic[%s] timed out", logicId), ErrorType.TEMPORARY);
	}
}
