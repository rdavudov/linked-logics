package io.linkedlogics.exception;

import io.linkedlogics.context.ContextError.ErrorType;
import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public class LogicException extends RuntimeException {
	private int errorCode;
	private String errorMessage;
	private ErrorType errorType;
	
	public LogicException(int errorCode, String errorMessage, ErrorType errorType, Throwable cause) {
		super(errorMessage, cause);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.errorType = errorType;
	}
	
	public LogicException(int errorCode, String errorMessage, ErrorType errorType) {
		super(errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.errorType = errorType;
	}
}
