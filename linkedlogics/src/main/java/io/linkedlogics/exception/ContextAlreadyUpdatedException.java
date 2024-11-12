package io.linkedlogics.exception;

@SuppressWarnings("serial")
public class ContextAlreadyUpdatedException extends RuntimeException {
	
	public ContextAlreadyUpdatedException(String id) {
		super(id + " context already modified");
	}
}

