package com.blockmar.letitrest.servlet;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Test;

import com.blockmar.letitrest.request.RequestHandler;
import com.blockmar.letitrest.request.exception.ForbiddenException;
import com.blockmar.letitrest.request.exception.NotFoundException;

public class DispatcherServletTest {
	
	@Test
	public void handlerNotFoundExceptionGenerates404() throws Exception {
		RequestHandler requestHandler = new TestRequestHandler(new NotFoundException());
		TestDispatcherServletConfig config = new TestDispatcherServletConfig(requestHandler);
		DispatcherServlet dispatcherServlet = new DispatcherServlet(config);
		
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		response.sendError(404, "Not Found");
		
		EasyMock.replay(request, response);
		
		dispatcherServlet.doGet(request, response);
		EasyMock.verify(request, response);
	}
	
	@Test
	public void handlerForbiddenExceptionGenerates403() throws Exception {
		RequestHandler requestHandler = new TestRequestHandler(new ForbiddenException());
		TestDispatcherServletConfig config = new TestDispatcherServletConfig(requestHandler);
		DispatcherServlet dispatcherServlet = new DispatcherServlet(config);
		
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		response.sendError(403, "Forbidden");
		
		EasyMock.replay(request, response);
		
		dispatcherServlet.doGet(request, response);
		EasyMock.verify(request, response);
	}
	
	private class TestRequestHandler extends RequestHandler {

		private RuntimeException e;
		
		public TestRequestHandler(DispatcherServletConfig servletConfig) {
			super(servletConfig);
		}
		
		public TestRequestHandler(RuntimeException e) {
			super(new DispatcherServletConfig() {
				@Override
				public Set<Object> getControllers() {
					return new HashSet<Object>();
				}
			});
			this.e = e;
		}
		
		@Override
		public void handle(HttpServletRequest request,
				HttpServletResponse response) {
			throw e;
		}
	}
	
	private class TestDispatcherServletConfig extends DispatcherServletConfig {
		
		private final RequestHandler requestHandler;
		
		public TestDispatcherServletConfig(RequestHandler requestHandler) {
			this.requestHandler = requestHandler;
		}

		@Override
		public Set<Object> getControllers() {
			return null;
		}
		
		@Override
		public RequestHandler getRequestHandler() {
			return requestHandler;
		}
	}
}
