package com.blockmar.letitrest.request;

import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blockmar.letitrest.request.exception.RequestMethodNotSupportedException;
import com.blockmar.letitrest.resolver.AnnotationScanner;
import com.blockmar.letitrest.resolver.MethodInvokationRequest;
import com.blockmar.letitrest.resolver.UrlResolver;
import com.blockmar.letitrest.servlet.DispatcherServletConfig;
import com.blockmar.letitrest.views.JsonRenderer;
import com.blockmar.letitrest.views.ViewAndModel;
import com.blockmar.letitrest.views.ViewRenderer;
import com.blockmar.letitrest.views.redirect.Redirect;

public class RequestHandler {

	private final UrlResolver urlResolver;

	private final MethodInvokationHandler invokationHandler;

	private final ViewRenderer defaultViewRenderer;
	private final ViewRenderer redirectViewRenderer;
	private final JsonRenderer jsonRenderer;

	@Inject
	public RequestHandler(DispatcherServletConfig servletConfig) {
		this.urlResolver = servletConfig.getUrlResolver();
		this.defaultViewRenderer = servletConfig.getDefaultViewRenderer();
		this.redirectViewRenderer = servletConfig.getRedirectViewRenderer();
		this.jsonRenderer = servletConfig.getDefaultJsonRenderer();
		this.invokationHandler = new MethodInvokationHandler();

		registerControllers(servletConfig.getControllers());
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		RequestMethod requestMethod = getRequestMethod(request);
		String requestURI = request.getRequestURI();
		MethodInvokationRequest urlHandler = urlResolver.resolveUrl(requestURI,
				requestMethod);
		renderUrlHandler(urlHandler, request, response);
	}

	private RequestMethod getRequestMethod(HttpServletRequest request) {
		String method = request.getMethod();
		try {
			return RequestMethod.valueOf(method);
		} catch (Exception e) {
			throw new RequestMethodNotSupportedException(
					"Method not supported: " + method);
		}
	}

	private void registerControllers(Set<Object> controllers) {
		AnnotationScanner scanner = new AnnotationScanner(urlResolver);
		for (Object controller : controllers) {
			scanner.scan(controller);
		}
	}

	private void renderUrlHandler(MethodInvokationRequest urlHandler,
			HttpServletRequest request, HttpServletResponse response) {
		Object invokationResult = invokationHandler.invoke(urlHandler, request);
		render(invokationResult, response);
	}

	private void render(Object objectToRender, HttpServletResponse response) {
		if (objectToRender instanceof ViewAndModel) {
			ViewAndModel viewAndModel = (ViewAndModel) objectToRender;
			ViewRenderer viewRenderer = viewAndModel.getViewRenderer();
			if (viewRenderer == ViewAndModel.DEFAULT_VIEW_RENDERER) {
				if (viewAndModel instanceof Redirect) {
					redirectViewRenderer.render(viewAndModel, response);
				} else {
					defaultViewRenderer.render(viewAndModel, response);
				}
			} else {
				viewRenderer.render(viewAndModel, response);
			}
		} else {
			jsonRenderer.render(objectToRender, response);
		}
	}
}
