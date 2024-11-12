package io.linkedlogics.context;

import java.lang.reflect.InvocationTargetException;

import io.linkedlogics.exception.LogicException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContextError {
	public static final int DEFAULT_ERROR_CODE = -1;
	
	public enum ErrorType { PERMANENT, TEMPORARY };
	
	private int code;
	private String message;
	private ErrorType type;
	
	public static ContextError of(Throwable e) {
		if (e instanceof InvocationTargetException) {
			e = ((InvocationTargetException) e).getCause();
		}
		
		if (e instanceof LogicException) {
			return new ContextError((((LogicException) e).getErrorCode()), (((LogicException) e).getErrorMessage()), (((LogicException) e).getErrorType()));
		} else {
			return new ContextError(DEFAULT_ERROR_CODE, e.getLocalizedMessage(), ErrorType.TEMPORARY);
		}
	}
	
	public String toString() {
		return new StringBuilder().append(code).append(" ").append(message).append(" ").append(type).toString();
	}
}
