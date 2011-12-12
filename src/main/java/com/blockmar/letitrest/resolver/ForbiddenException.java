package com.blockmar.letitrest.resolver;

public class ForbiddenException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ForbiddenException() {
		super();	
	}
	
	public ForbiddenException(String message) {
		super(message);
	}

}
