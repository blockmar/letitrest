package com.blockmar.letitrest.request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blockmar.letitrest.request.exception.RequestMethodNotSupportedException;
import com.blockmar.letitrest.resolver.AnnotationScanner;
import com.blockmar.letitrest.resolver.UrlResolver;
import com.blockmar.letitrest.resolver.UrlResolverResult;
import com.blockmar.letitrest.servlet.DispatcherServletConfig;
import com.blockmar.letitrest.views.ViewAndModel;
import com.blockmar.letitrest.views.ViewRenderer;

//TODO Refactoring. Can we split this to single responsibility?
public class RequestHandler {
	
	private static final String REDIRECT_PREFIX = "redirect:";
	
	private final UrlResolver urlResolver;
	
	private final ViewRenderer defaultViewRenderer;
	private final ViewRenderer redirectViewRenderer;
	private final ViewRenderer jsonViewRenderer;
	
	public RequestHandler(DispatcherServletConfig servletConfig) {
		this.urlResolver = servletConfig.getUrlResolver();
		this.defaultViewRenderer = servletConfig.getDefaultViewRenderer();
		this.redirectViewRenderer = servletConfig.getRedirectViewRenderer();
		this.jsonViewRenderer = servletConfig.getJsonViewRenderer();
		
		registerControllers(servletConfig.getControllers());
	}	

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		RequestMethod requestMethod = getRequestMethod(request);
		String requestURI = request.getRequestURI();
		UrlResolverResult urlHandler = urlResolver.resolveUrl(requestURI,
				requestMethod);
		renderUrlHandler(urlHandler, request, response);
	}
	
	private RequestMethod getRequestMethod(HttpServletRequest request) {
		String method = request.getMethod();
		try {
			return RequestMethod.valueOf(method);
		} catch (Exception e) {
			throw new RequestMethodNotSupportedException("Method not supported: " + method);
		}
	}

	private void registerControllers(Set<Object> controllers) {
		AnnotationScanner scanner = new AnnotationScanner(urlResolver);
		for (Object controller : controllers) {
			scanner.scan(controller);
		}
	}
	
	private void renderUrlHandler(UrlResolverResult urlHandler,
			HttpServletRequest request, HttpServletResponse response) {
		ViewAndModel viewAndModel = invoke(urlHandler, request);
		render(viewAndModel, response);
	}
	
	private void render(ViewAndModel viewAndModel, HttpServletResponse response) {
		ViewRenderer viewRenderer = viewAndModel.getViewRenderer();
		if (viewRenderer == ViewAndModel.DEFAULT_VIEW_RENDERER) {
			defaultViewRenderer.render(viewAndModel, response);
		} else {
			viewRenderer.render(viewAndModel, response);
		}
	}
	
	private ViewAndModel invoke(UrlResolverResult urlHandler,
			HttpServletRequest request) {

		try {
			Object methodResult = invokeMethod(urlHandler, request);

			if (methodResult instanceof ViewAndModel) {
				ViewAndModel viewAndModel = (ViewAndModel) methodResult;
				// TODO Not a good solution
				if ("json".equals(viewAndModel.getView().toLowerCase())) {
					viewAndModel.setViewRenderer(jsonViewRenderer);
				}
				return viewAndModel;
			} else if (methodResult instanceof String) {
				String methodResultString = (String) methodResult;
				if (!methodResultString.startsWith(REDIRECT_PREFIX)) {
					return new ViewAndModel(methodResultString);
				} else {
					return createRedirect(methodResultString);
				}
			} else {
				throw new UnsupportedOperationException(
						"Can't handle methods with return type: "
								+ urlHandler.getMethod().getReturnType()
										.toString());
			}

		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Object invokeMethod(UrlResolverResult urlHandler,
			HttpServletRequest request) throws IllegalAccessException,
			InvocationTargetException {
		Object methodResult;
		Method method = urlHandler.getMethod();
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 0) {
			methodResult = method.invoke(urlHandler.getInstance());
		} else if (parameterTypes.length == 1
				&& parameterTypes[0].equals(HttpServletRequest.class)) {
			methodResult = method.invoke(urlHandler.getInstance(), request);
		} else {
			String[] urlParams = urlHandler.getUrlParameters();
			if (parameterTypes.length == urlParams.length) {
				methodResult = method.invoke(urlHandler.getInstance(),
						(Object[]) urlParams);
			} else {
				throw new UnsupportedOperationException(
						"Can't handle methods with this signature.");
			}
		}
		return methodResult;
	}

	private ViewAndModel createRedirect(String methodResultString) {
		String url = methodResultString.substring(REDIRECT_PREFIX.length())
				.trim();
		ViewAndModel viewAndModel = new ViewAndModel(url);
		viewAndModel.setViewRenderer(redirectViewRenderer);
		return viewAndModel;
	}
}
