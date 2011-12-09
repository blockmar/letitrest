package com.blockmar.letitrest.resolver.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.blockmar.letitrest.resolver.NoMatchFoundException;
import com.blockmar.letitrest.resolver.UrlResolver;
import com.blockmar.letitrest.resolver.UrlResolverResult;
import com.blockmar.letitrest.servlet.RequestMethod;

public class PatternUrlResolver implements UrlResolver {
	
	private final static RequestMethod[] ALL_REQUEST_MOTHODS = new RequestMethod[]{ RequestMethod.GET, RequestMethod.POST};

	private final List<PatternAndHandler> getMappings = new ArrayList<PatternUrlResolver.PatternAndHandler>();
	private final List<PatternAndHandler> postMappings = new ArrayList<PatternUrlResolver.PatternAndHandler>();
	
	@Override
	public UrlResolverResult resolveUrl(String url, RequestMethod method) throws NoMatchFoundException {
		for (PatternAndHandler patternAndHandler : (method == RequestMethod.GET ? getMappings : postMappings)) {
			Pattern pattern = patternAndHandler.getPattern();
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				return createResolverResult(patternAndHandler, matcher);
			}
		}
		throw new NoMatchFoundException("No mapping found for URL: " + url);
	}

	@Override
	public void registerUrl(String urlPattern, Object classInstance, Method method) {
		registerUrl(urlPattern, classInstance, method, ALL_REQUEST_MOTHODS);
	}

	@Override
	public void registerUrl(String urlPattern, Object classInstance, Method method, RequestMethod[] methods) {
		
		Pattern pattern = Pattern.compile("^" + urlPattern + "$");
		PatternAndHandler patternAndHandler = new PatternAndHandler(pattern, new UrlHandler(classInstance, method));
		
		for (RequestMethod requestMethod : methods) {
			switch (requestMethod) {
			case GET:
				getMappings.add(patternAndHandler);
				break;
				
			case POST:
				postMappings.add(patternAndHandler);
				break;

			default:
				throw new UnsupportedOperationException("Request method not supported: " + requestMethod.name());
			}
		}
	}

	private UrlResolverResult createResolverResult(
			PatternAndHandler patternAndHandler, Matcher matcher) {
		
		MatchResult result = matcher.toMatchResult();
		int groupCount = result.groupCount();
		String[] urlParams = new String[groupCount];
		for (int i = 0; i < groupCount; i++) {
			urlParams[i] = result.group(i + 1); //Group 0 is entire match
		}
		
		UrlHandler handler = patternAndHandler.getHandler();
		return new UrlResolverResult(handler.getInstance(), handler.getMethod(), urlParams);
	}
	
	private class PatternAndHandler {
		
		private final Pattern pattern;
		private final UrlHandler handler;

		public PatternAndHandler(Pattern pattern, UrlHandler handler) {
			this.pattern = pattern;
			this.handler = handler;
		}

		public Pattern getPattern() {
			return pattern;
		}

		public UrlHandler getHandler() {
			return handler;
		}
	}
	
	private class UrlHandler {
		
		private final Object instance;
		private final Method method;

		public UrlHandler(Object instance, Method method) {
			this.instance = instance;
			this.method = method;
		}

		public Object getInstance() {
			return instance;
		}

		public Method getMethod() {
			return method;
		}
	}
}
