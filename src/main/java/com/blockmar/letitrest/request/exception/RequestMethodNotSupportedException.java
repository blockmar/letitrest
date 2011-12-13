package com.blockmar.letitrest.request.exception;

public class RequestMethodNotSupportedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public RequestMethodNotSupportedException(String message) {
		super(message);
	}

}
