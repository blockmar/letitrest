package com.blockmar.letitrest.resolver;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.blockmar.letitrest.request.RequestMethod;
import com.blockmar.letitrest.request.annotation.FallbackRequestMapping;
import com.blockmar.letitrest.request.annotation.RequestMapping;

public class AnnotationScanner {

	private final Logger logger = Logger.getLogger(getClass());

	private final UrlResolver urlResolver;

	public AnnotationScanner(UrlResolver urlResolver) {
		this.urlResolver = urlResolver;
	}

	public void scan(Object controller) {
		logger.debug("Scanning " + controller.getClass().getCanonicalName());
		Method[] methods = controller.getClass().getMethods();
		for (Method method : methods) {			
			scanForRequestMapping(controller, method);
			scanForFallbackRequestMapping(controller, method);
		}
	}

	private void scanForRequestMapping(Object controller, Method method) {
		RequestMapping requestMapping = method
				.getAnnotation(RequestMapping.class);
		if (requestMapping != null) {
			String url = requestMapping.value();
			RequestMethod[] requestMethods = requestMapping.requestMethod();
			urlResolver.registerUrl(url, controller, method, requestMethods);
		}
	}

	private void scanForFallbackRequestMapping(Object controller, Method method) {
		FallbackRequestMapping fallbackRequestMapping = method
				.getAnnotation(FallbackRequestMapping.class);
		if (fallbackRequestMapping != null) {
			RequestMethod[] requestMethods = fallbackRequestMapping
					.requestMethod();
			urlResolver.registerFallbackUrl(controller, method, requestMethods);
		}
	}
}
