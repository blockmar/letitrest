package com.blockmar.letitrest.resolver.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.blockmar.letitrest.resolver.NoMatchFoundException;
import com.blockmar.letitrest.resolver.RequestMethodNotSupportedException;
import com.blockmar.letitrest.resolver.UrlResolver;
import com.blockmar.letitrest.resolver.UrlResolverResult;
import com.blockmar.letitrest.servlet.RequestMethod;

public class PatternUrlResolver implements UrlResolver {

	private final static RequestMethod[] ALL_REQUEST_MOTHODS = new RequestMethod[] {
			RequestMethod.GET, RequestMethod.POST };

	private final List<PatternAndHandler> urls = new ArrayList<PatternUrlResolver.PatternAndHandler>();

	@Override
	public UrlResolverResult resolveUrl(String url, RequestMethod method)
			throws NoMatchFoundException {

		//TODO Is this good or just a performance theif.
		//Create a list of candidates, this allows different matches for GET and POST and
		//still reporting Request method not supported error intead of not found.
		//Downside is that all url:s are scanned even if a match is found early.
		
		List<PatternAndHandler> candidates = new ArrayList<PatternUrlResolver.PatternAndHandler>();

		Matcher candidateMatcher = null;
		for (PatternAndHandler patternAndHandler : urls) {
			Pattern pattern = patternAndHandler.getPattern();
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				candidates.add(patternAndHandler);
				candidateMatcher = matcher;
			}
		}

		if (candidates.size() == 0) {
			throw new NoMatchFoundException("No mapping found for URL: " + url);
		}

		for (PatternAndHandler patternAndHandler : candidates) {
			RequestMethod[] requestMethods = patternAndHandler
					.getRequestMethods();
			for (int i = 0; i < requestMethods.length; i++) {
				if (requestMethods[i] == method) {
					return createResolverResult(patternAndHandler,
							candidateMatcher);
				}
			}
		}

		throw new RequestMethodNotSupportedException(String.format(
				"Method %s not supported for URL %s", method.name(), url));
	}

	@Override
	public void registerUrl(String urlPattern, Object classInstance,
			Method method) {
		registerUrl(urlPattern, classInstance, method, ALL_REQUEST_MOTHODS);
	}

	@Override
	public void registerUrl(String urlPattern, Object classInstance,
			Method method, RequestMethod... requestMethods) {
		Pattern pattern = Pattern.compile("^" + urlPattern + "$");
		PatternAndHandler patternAndHandler = new PatternAndHandler(pattern,
				requestMethods, classInstance, method);
		urls.add(patternAndHandler);
	}

	private UrlResolverResult createResolverResult(
			PatternAndHandler patternAndHandler, Matcher matcher) {

		MatchResult result = matcher.toMatchResult();
		int groupCount = result.groupCount();
		String[] urlParams = new String[groupCount];
		for (int i = 0; i < groupCount; i++) {
			urlParams[i] = result.group(i + 1); // Group 0 is entire match
		}

		return new UrlResolverResult(patternAndHandler.getInstance(),
				patternAndHandler.getMethod(), urlParams);
	}

	private class PatternAndHandler {

		private final Pattern pattern;
		private final RequestMethod[] requestMethods;
		private final Object instance;
		private final Method method;

		public PatternAndHandler(Pattern pattern,
				RequestMethod[] requestMethods, Object instance, Method method) {
			this.pattern = pattern;
			this.requestMethods = requestMethods;
			this.instance = instance;
			this.method = method;
		}

		public Pattern getPattern() {
			return pattern;
		}

		public Object getInstance() {
			return instance;
		}

		public Method getMethod() {
			return method;
		}

		public RequestMethod[] getRequestMethods() {
			return requestMethods;
		}
	}
}
