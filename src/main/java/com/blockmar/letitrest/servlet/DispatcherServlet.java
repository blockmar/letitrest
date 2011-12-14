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
import com.blockmar.letitrest.request.exception.ForbiddenException;
import com.blockmar.letitrest.request.exception.NotFoundException;
import com.blockmar.letitrest.request.exception.RequestMethodNotSupportedException;
import com.blockmar.letitrest.resolver.AnnotationScanner;
import com.blockmar.letitrest.resolver.UrlResolver;
import com.blockmar.letitrest.resolver.UrlResolverResult;
import com.blockmar.letitrest.views.ViewAndModel;
import com.blockmar.letitrest.views.ViewRenderer;

//TODO This needs refactoring
public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String REDIRECT_PREFIX = "redirect:";

	private final Logger logger = Logger.getLogger(getClass());

	private final UrlResolver urlResolver;

	private final ViewRenderer defaultViewRenderer;
	private final ViewRenderer redirectViewRenderer;
	private final ViewRenderer jsonViewRenderer;

	@Inject
	public DispatcherServlet(DispatcherServletConfig servletConfig) {
		this.urlResolver = servletConfig.getUrlResolver();
		this.defaultViewRenderer = servletConfig.getDefaultViewRenderer();
		this.redirectViewRenderer = servletConfig.getRedirectViewRenderer();
		this.jsonViewRenderer = servletConfig.getJsonViewRenderer();
		registerControllers(servletConfig.getControllers());
	}

	private void registerControllers(Set<Object> controllers) {
		AnnotationScanner scanner = new AnnotationScanner(urlResolver);
		for (Object controller : controllers) {
			scanner.scan(controller);
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
			String requestURI = request.getRequestURI();
			UrlResolverResult urlHandler = urlResolver.resolveUrl(requestURI,
					requestMethod);
			renderUrlHandler(urlHandler, request, response);
		} catch (NotFoundException e) {
			sendNotFound(response, e.getMessage());
		} catch (ForbiddenException e) {
			sendForbidden(response, e.getMessage());
		} catch (RequestMethodNotSupportedException e) {
			sendMethodNotSupported(response, request, e.getMessage());
		}
	}

	private void sendMethodNotSupported(HttpServletResponse response,
			HttpServletRequest request, String message) throws IOException {
		logger.info(message);
		String msg = "Method not supported";
		if (request.getProtocol().endsWith("1.1")) {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
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

	protected ViewAndModel invoke(UrlResolverResult urlHandler,
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
