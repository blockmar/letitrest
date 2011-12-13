package com.blockmar.letitrest.resolver.regexp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.blockmar.letitrest.request.RequestMethod;
import com.blockmar.letitrest.request.exception.NotFoundException;
import com.blockmar.letitrest.request.exception.RequestMethodNotSupportedException;
import com.blockmar.letitrest.resolver.UrlResolver;
import com.blockmar.letitrest.resolver.UrlResolverResult;

public class PatternUrlResolver implements UrlResolver {

	private final Logger logger = Logger.getLogger(getClass());

	private final static RequestMethod[] ALL_REQUEST_MOTHODS = new RequestMethod[] {
			RequestMethod.GET, RequestMethod.POST };

	private final List<PatternHandler> urlHandlers = new ArrayList<PatternHandler>();
	private final Map<RequestMethod, Handler> fallbackHandlers = new HashMap<RequestMethod, Handler>();

	@Override
	public UrlResolverResult resolveUrl(String url, RequestMethod requestMethod)
			throws NotFoundException {

		// TODO Is this good or just a performance theif.
		// Create a list of candidates, this allows different matches for GET
		// and POST and
		// still reporting Request method not supported error intead of not
		// found.
		// Downside is that all url:s are scanned even if a match is found
		// early.

		List<PatternHandler> candidates = new ArrayList<PatternHandler>();

		Matcher candidateMatcher = null;
		for (PatternHandler patternHandler : urlHandlers) {
			Pattern pattern = patternHandler.getPattern();
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				candidates.add(patternHandler);
				candidateMatcher = matcher;
			}
		}

		if (candidates.size() == 0) {
			throw new NotFoundException("No mapping found for URL: " + url);
		}

		for (PatternHandler patternHandler : candidates) {
			RequestMethod[] requestMethods = patternHandler.getRequestMethods();
			for (int i = 0; i < requestMethods.length; i++) {
				if (requestMethods[i] == requestMethod) {
					return createResolverResult(patternHandler,
							candidateMatcher);
				}
			}
		}

		Handler fallbackHandler = fallbackHandlers.get(requestMethod);
		if (fallbackHandler != null) {
			return createResolverResult(fallbackHandler);
		}

		throw new RequestMethodNotSupportedException(
				String.format("Method %s not supported for URL %s",
						requestMethod.name(), url));
	}

	@Override
	public void registerUrl(String urlPattern, Object classInstance,
			Method method, RequestMethod... requestMethods) {

		if (requestMethods.length == 0) {
			requestMethods = ALL_REQUEST_MOTHODS;
		}

		Pattern pattern = Pattern.compile("^" + urlPattern + "$");
		PatternHandler patternHandler = new PatternHandler(pattern,
				requestMethods, classInstance, method);
		urlHandlers.add(patternHandler);

		logger.debug("Regestering " + urlPattern + " to method "
				+ method.getName() + ", request methods: "
				+ requestMethodsToString(requestMethods));
	}

	@Override
	public void registerFallbackUrl(Object classInstance, Method method,
			RequestMethod... requestMethods) {

		if (requestMethods.length == 0) {
			requestMethods = ALL_REQUEST_MOTHODS;
		}

		for (RequestMethod requestMethod : requestMethods) {
			fallbackHandlers.put(requestMethod, new Handler(classInstance,
					method));
			logger.debug("Regestering fallback to method " + method.getName()
					+ ", request methods: " + requestMethod.name());
		}
	}

	private UrlResolverResult createResolverResult(
			PatternHandler patternHandler, Matcher matcher) {

		MatchResult result = matcher.toMatchResult();
		int groupCount = result.groupCount();
		String[] urlParams = new String[groupCount];
		for (int i = 0; i < groupCount; i++) {
			urlParams[i] = result.group(i + 1); // Group 0 is entire match
		}

		return new UrlResolverResult(patternHandler.getInstance(),
				patternHandler.getMethod(), urlParams);
	}

	private UrlResolverResult createResolverResult(Handler fallbackHandler) {
		return new UrlResolverResult(fallbackHandler.getInstance(),
				fallbackHandler.getMethod(), new String[0]);
	}

	private String requestMethodsToString(RequestMethod[] requestMethods) {
		StringBuffer stringBuffer = new StringBuffer();
		for (RequestMethod requestMethod : requestMethods) {
			stringBuffer.append(requestMethod.name());
			stringBuffer.append(" ");
		}
		return stringBuffer.toString();
	}

	private class PatternHandler extends Handler {

		private final Pattern pattern;
		private final RequestMethod[] requestMethods;

		public PatternHandler(Pattern pattern, RequestMethod[] requestMethods,
				Object instance, Method method) {
			super(instance, method);
			this.pattern = pattern;
			this.requestMethods = requestMethods;
		}

		public Pattern getPattern() {
			return pattern;
		}

		public RequestMethod[] getRequestMethods() {
			return requestMethods;
		}
	}

	private class Handler {

		private final Object instance;
		private final Method method;

		public Handler(Object instance, Method method) {
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
