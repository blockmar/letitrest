package com.blockmar.letitrest.resolver;

import java.lang.reflect.Method;

import com.blockmar.letitrest.request.RequestMethod;
import com.blockmar.letitrest.request.exception.NotFoundException;

public interface UrlResolver {
	
	public void registerUrl(String url, Object classInstance, Method method, RequestMethod... requestMethods);
	
	public void registerFallbackUrl(Object classInstance, Method method, RequestMethod... requestMethods);
	
	public MethodInvokationRequest resolveUrl(String url, RequestMethod method) throws NotFoundException;
	
}
