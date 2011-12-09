package com.blockmar.letitrest.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.blockmar.letitrest.resolver.NoMatchFoundException;
import com.blockmar.letitrest.resolver.UrlResolver;
import com.blockmar.letitrest.resolver.UrlResolverResult;
import com.blockmar.letitrest.views.ViewAndModel;
import com.blockmar.letitrest.views.ViewRenderer;

public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final Logger logger = Logger.getLogger(getClass());

	private final UrlResolver urlResolver;
	private final ViewRenderer viewRenderer;

	public DispatcherServlet(DispatcherServletConfig servletConfig) {
		this.urlResolver = servletConfig.getUrlResolver();
		this.viewRenderer = servletConfig.getViewRenderer();
		registerControllers(servletConfig.getControllers());
	}

	private void registerControllers(Set<Object> controllers) {
		for (Object controller : controllers) {
			registerController(controller);
		}
	}

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
		try {
			UrlResolverResult urlHandler = urlResolver.resolveUrl(
					request.getRequestURI(), RequestMethod.GET);
			invokeGet(urlHandler, request, response);
		} catch (NoMatchFoundException e) {
			replyWithError(HttpServletResponse.SC_NOT_FOUND, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// Unsupported so far.
		super.doPost(req, resp);
	}

	private void replyWithError(int errorStatus, HttpServletResponse response) {
		response.setStatus(errorStatus);
	}
	
	private void invokeGet(UrlResolverResult urlHandler,
			HttpServletRequest request, HttpServletResponse response) {
		ViewAndModel viewAndModel = invoke(urlHandler, request, response);
		viewRenderer.render(viewAndModel, response);
	}

	protected ViewAndModel invoke(UrlResolverResult urlHandler,
			HttpServletRequest request, HttpServletResponse response) {

		try {
			Method method = urlHandler.getMethod();
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length == 0) {
				return (ViewAndModel) method.invoke(urlHandler.getInstance());
			} else if (parameterTypes.length == 1
					&& parameterTypes[0].equals(HttpServletRequest.class)) {
				return (ViewAndModel) method.invoke(urlHandler.getInstance(),
						request);
			} else {
				String[] urlParams = urlHandler.getUrlParameters();
				if (parameterTypes.length == urlParams.length) {
					return (ViewAndModel) method.invoke(
							urlHandler.getInstance(), (Object[]) urlParams);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		throw new UnsupportedOperationException(
				"Can't handle methods with this signature.");
	}
}
