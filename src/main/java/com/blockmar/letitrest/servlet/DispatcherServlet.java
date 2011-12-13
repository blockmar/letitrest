package com.blockmar.letitrest.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.blockmar.letitrest.request.RequestMethod;
import com.blockmar.letitrest.request.annotation.RequestMapping;
import com.blockmar.letitrest.request.exception.ForbiddenException;
import com.blockmar.letitrest.request.exception.NotFoundException;
import com.blockmar.letitrest.request.exception.RequestMethodNotSupportedException;
import com.blockmar.letitrest.resolver.UrlResolver;
import com.blockmar.letitrest.resolver.UrlResolverResult;
import com.blockmar.letitrest.views.ViewAndModel;
import com.blockmar.letitrest.views.ViewRenderer;

public class DispatcherServlet extends HttpServlet {

	private static final String REDIRECT_PREFIX = "redirect:";

	private static final long serialVersionUID = 1L;

	private final Logger logger = Logger.getLogger(getClass());

	private final UrlResolver urlResolver;
	private final ViewRenderer defaultViewRenderer;
	private final ViewRenderer redirectViewRenderer;

	@Inject
	public DispatcherServlet(DispatcherServletConfig servletConfig) {
		this.urlResolver = servletConfig.getUrlResolver();
		this.defaultViewRenderer = servletConfig.getDefaultViewRenderer();
		this.redirectViewRenderer = servletConfig.getRedirectViewRenderer();
		registerControllers(servletConfig.getControllers());
	}

	private void registerControllers(Set<Object> controllers) {
		for (Object controller : controllers) {
			registerController(controller);
		}
	}

	// TODO Scan for fallback mapping
	private void registerController(Object controller) {
		logger.debug("Scanning: " + controller.getClass().getCanonicalName());
		Method[] methods = controller.getClass().getMethods();
		for (Method method : methods) {
			RequestMapping requestMapping = method
					.getAnnotation(RequestMapping.class);
			if (requestMapping != null) {
				String url = requestMapping.value();
				RequestMethod[] requestMethods = requestMapping.requestMethod();
				if (requestMethods.length == 0) {
					logger.debug("Regestering " + requestMapping.value()
							+ " to method " + method.getName());
					urlResolver.registerUrl(url, controller, method);
				} else {
					logger.debug("Regestering " + requestMapping.value()
							+ " to method " + method.getName()
							+ ", request methods: " + requestMethods);
					urlResolver.registerUrl(url, controller, method,
							requestMethods);
				}
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response, RequestMethod.GET);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response, RequestMethod.POST);
	}

	private void handleRequest(HttpServletRequest request,
			HttpServletResponse response, RequestMethod requestMethod)
			throws IOException {
		try {
			UrlResolverResult urlHandler = urlResolver.resolveUrl(
					request.getRequestURI(), requestMethod);
			invokeMethod(urlHandler, request, response);
		} catch (NotFoundException e) {
			sendNotFound(response, e.getMessage());
		} catch (ForbiddenException e) {
			sendForbidden(response, e.getMessage());
		} catch (RequestMethodNotSupportedException e) {
			logger.info(e.getMessage());
			String msg = "Method not supported";
			if (request.getProtocol().endsWith("1.1")) {
				response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
						msg);
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
			}
		}
	}

	private void sendNotFound(HttpServletResponse response, String message)
			throws IOException {
		logger.info(message);
		response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
	}

	private void sendForbidden(HttpServletResponse response, String message)
			throws IOException {
		logger.info(message);
		response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
	}

	private void invokeMethod(UrlResolverResult urlHandler,
			HttpServletRequest request, HttpServletResponse response) {
		ViewAndModel viewAndModel = invoke(urlHandler, request, response);
		ViewRenderer viewRenderer = viewAndModel.getViewRenderer();
		if (viewRenderer == ViewAndModel.DEFAULT_VIEW_RENDERER) {
			defaultViewRenderer.render(viewAndModel, response);
		} else {
			viewRenderer.render(viewAndModel, response);
		}
	}

	protected ViewAndModel invoke(UrlResolverResult urlHandler,
			HttpServletRequest request, HttpServletResponse response) {

		try {
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

			if (methodResult instanceof ViewAndModel) {
				return (ViewAndModel) methodResult;
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
								+ method.getReturnType().toString());
			}

		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if(cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private ViewAndModel createRedirect(String methodResultString) {
		String url = methodResultString.substring(REDIRECT_PREFIX.length())
				.trim();
		ViewAndModel viewAndModel = new ViewAndModel(url);
		viewAndModel.setViewRenderer(redirectViewRenderer);
		return viewAndModel;
	}
}
