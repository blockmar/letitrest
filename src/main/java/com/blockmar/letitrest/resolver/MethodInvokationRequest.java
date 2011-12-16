package com.blockmar.letitrest.resolver;

import java.lang.reflect.Method;

public class MethodInvokationRequest {
	
	private final Object instance;
	private final Method method;
	private final String[] urlParams;

	public MethodInvokationRequest(Object instance, Method method, String[] urlParams) {
		this.instance = instance;
		this.method = method;
		this.urlParams = urlParams;
	}

	public Object getInstance() {
		return instance;
	}

	public Method getMethod() {
		return method;
	}

	public String[] getUrlParameters() {
		return urlParams;
	}
}
