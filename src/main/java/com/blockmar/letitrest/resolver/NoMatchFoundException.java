package com.blockmar.letitrest.resolver;

public class NoMatchFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public NoMatchFoundException(String message) {
		super(message);
	}

}
