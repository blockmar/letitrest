package com.blockmar.letitrest.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final Logger logger = Logger.getLogger(getClass());
	
	protected final Map<String, UrlMethod> urlMap = new HashMap<String, UrlMethod>();
	
	private final DispatcherServletConfig servletConfig;

	public DispatcherServlet(DispatcherServletConfig servletConfig) {
		this.servletConfig = servletConfig;
		
		registerControllers();
	}

	private void registerControllers() {
		for (Object controller : servletConfig.getControllers()) {
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
				logger.debug("Regestering " + requestMapping.value()
						+ " to method " + method.getName());
				urlMap.put(requestMapping.value(), new UrlMethod(controller,
						method));
			}
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("Hello, world!");
		out.close();
	}
	
	static class UrlMethod {

		private final Object instance;
		private final Method method;

		UrlMethod(Object instance, Method method) {
			this.instance = instance;
			this.method = method;
		}

		Object getInstance() {
			return instance;
		}

		Method getMethod() {
			return method;
		}
	}
}
