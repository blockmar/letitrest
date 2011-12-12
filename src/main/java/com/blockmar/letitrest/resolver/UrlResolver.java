package com.blockmar.letitrest.resolver;

import java.lang.reflect.Method;

import com.blockmar.letitrest.servlet.RequestMethod;

public interface UrlResolver {
	
	public void registerUrl(String url, Object classInstance, Method method);
	
	public void registerUrl(String url, Object classInstance, Method method, RequestMethod... requestMethods);
	
	public UrlResolverResult resolveUrl(String url, RequestMethod method) throws NoMatchFoundException;
}
