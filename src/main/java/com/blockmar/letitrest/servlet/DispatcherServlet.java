package com.blockmar.letitrest.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.blockmar.letitrest.request.RequestHandler;
import com.blockmar.letitrest.request.exception.ForbiddenException;
import com.blockmar.letitrest.request.exception.NotFoundException;
import com.blockmar.letitrest.request.exception.RequestMethodNotSupportedException;

public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String SERVLET_CONFIG_PARAMETER = "servletConfig";

	private static final String ERROR_SERVLET_CONFIG_INCORRECT_TYPE = "Class servlet-config not of type DispatcherServletConfig!";
	private static final String ERROR_SERVLET_CONFIG_PARAMETER_MISSING = "Init paramter servletConfig is missing!";

	private static final String HTTP_ERROR_FORBIDDEN = "Forbidden";
	private static final String HTTP_ERROR_NOT_FOUND = "Not Found";
	private static final String HTTP_ERROR_METHOD_NOT_SUPPORTED = "Method not supported";

	private final Logger logger = Logger.getLogger(getClass());

	private DispatcherServletConfig servletConfig;
	private RequestHandler requestHandler;

	/**
	 * Used by servlet container, requires call to init(ServletConfig).
	 */
	public DispatcherServlet() {
		super();
	}

	@Inject
	public DispatcherServlet(DispatcherServletConfig servletConfig) {
		super();
		this.servletConfig = servletConfig;
		this.requestHandler = servletConfig.getRequestHandler();
	}

	@Override
	public void init() throws ServletException {
		if(servletConfig == null) {
			servletConfig = getServletConfigFromInitParameter();
			requestHandler = servletConfig.getRequestHandler();
		}		
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}

	private void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		try {
			requestHandler.handle(request, response);
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
		if (request.getProtocol().endsWith("1.1")) {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
					HTTP_ERROR_METHOD_NOT_SUPPORTED);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					HTTP_ERROR_METHOD_NOT_SUPPORTED);
		}
	}

	private void sendNotFound(HttpServletResponse response, String message)
			throws IOException {
		logger.info(message);
		response.sendError(HttpServletResponse.SC_NOT_FOUND,
				HTTP_ERROR_NOT_FOUND);
	}

	private void sendForbidden(HttpServletResponse response, String message)
			throws IOException {
		logger.info(message);
		response.sendError(HttpServletResponse.SC_FORBIDDEN,
				HTTP_ERROR_FORBIDDEN);
	}

	private DispatcherServletConfig getServletConfigFromInitParameter()
			throws ServletException {
		String initParameter = getServletConfig().getInitParameter(
				SERVLET_CONFIG_PARAMETER);
		if (initParameter == null) {
			throw new ServletException(ERROR_SERVLET_CONFIG_PARAMETER_MISSING);
		}
		Object servletConfig;
		try {
			servletConfig = getClass().getClassLoader()
					.loadClass(initParameter).newInstance();
		} catch (Exception e) {
			throw new ServletException(e);
		}
		if (!(servletConfig instanceof DispatcherServletConfig)) {
			throw new ServletException(ERROR_SERVLET_CONFIG_INCORRECT_TYPE);
		}
		return (DispatcherServletConfig) servletConfig;
	}
}
