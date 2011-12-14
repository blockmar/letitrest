package com.blockmar.letitrest.request;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Test;

import com.blockmar.letitrest.request.annotation.RequestMapping;
import com.blockmar.letitrest.request.exception.ForbiddenException;
import com.blockmar.letitrest.request.exception.NotFoundException;
import com.blockmar.letitrest.request.exception.RequestMethodNotSupportedException;
import com.blockmar.letitrest.servlet.DispatcherServletConfig;
import com.blockmar.letitrest.views.ViewAndModel;
import com.blockmar.letitrest.views.ViewRenderer;

public class RequestHandlerTest {

	@Test
	public void standardUrlsUseDefaultRenderer() throws Exception {
		TestDispatcherServlet servletConfig = new TestDispatcherServlet();
		RequestHandler requestHandler = new RequestHandler(servletConfig);
		
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getMethod()).andReturn("GET");
		EasyMock.expect(request.getRequestURI()).andReturn("/test");		
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		response.setStatus(HttpServletResponse.SC_OK);
		
		EasyMock.replay(request, response);
		
		requestHandler.handle(request, response);
		EasyMock.verify(request, response);
	}
	
	@Test
	public void standardPostUrlUseDefaultRenderer() throws Exception {
		TestDispatcherServlet servletConfig = new TestDispatcherServlet();
		RequestHandler requestHandler = new RequestHandler(servletConfig);
		
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getMethod()).andReturn("POST");
		EasyMock.expect(request.getRequestURI()).andReturn("/post");		
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		response.setStatus(HttpServletResponse.SC_OK);
		
		EasyMock.replay(request, response);
		
		requestHandler.handle(request, response);
		EasyMock.verify(request, response);
	}
	
	@Test
	public void redirectStringPrefixSendsRedirect() throws Exception {
		TestDispatcherServlet servletConfig = new TestDispatcherServlet();
		RequestHandler requestHandler = new RequestHandler(servletConfig);
		
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getMethod()).andReturn("GET");
		EasyMock.expect(request.getRequestURI()).andReturn("/redirect");		
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		response.sendRedirect("/");
		
		EasyMock.replay(request, response);
		
		requestHandler.handle(request, response);
		EasyMock.verify(request, response);
	}
	
	@Test(expected=NotFoundException.class)
	public void controllerThrownNotFoundPropagates() throws Exception {
		TestDispatcherServlet servletConfig = new TestDispatcherServlet();
		RequestHandler requestHandler = new RequestHandler(servletConfig);
		
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getMethod()).andReturn("GET");
		EasyMock.expect(request.getRequestURI()).andReturn("/existsbutthrows404");		
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);		
		
		EasyMock.replay(request, response);
		
		requestHandler.handle(request, response);
	}
	
	@Test(expected=ForbiddenException.class)
	public void controllerThrownForbiddenPropagates() throws Exception {
		TestDispatcherServlet servletConfig = new TestDispatcherServlet();
		RequestHandler requestHandler = new RequestHandler(servletConfig);
		
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getMethod()).andReturn("GET");
		EasyMock.expect(request.getRequestURI()).andReturn("/existsbutthrows403");		
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		
		EasyMock.replay(request, response);
		
		requestHandler.handle(request, response);
	}
	
	@Test(expected=RequestMethodNotSupportedException.class)
	public void unsupportedRequestMethosThrowsException() throws Exception {
		TestDispatcherServlet servletConfig = new TestDispatcherServlet();
		RequestHandler requestHandler = new RequestHandler(servletConfig);
		
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getMethod()).andReturn("PUT");
		EasyMock.expect(request.getRequestURI()).andReturn("/test");		
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		
		EasyMock.replay(request, response);
		
		requestHandler.handle(request, response);
	}
	
	@Test(expected=RequestMethodNotSupportedException.class)
	public void incorrectRequestMethosThrowsException() throws Exception {
		TestDispatcherServlet servletConfig = new TestDispatcherServlet();
		RequestHandler requestHandler = new RequestHandler(servletConfig);
		
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getMethod()).andReturn("GET");
		EasyMock.expect(request.getRequestURI()).andReturn("/post");		
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		
		EasyMock.replay(request, response);
		
		requestHandler.handle(request, response);
	}
	
	private class TestDispatcherServlet extends DispatcherServletConfig {
		@Override
		public Set<Object> getControllers() {
			Set<Object> controllers = new HashSet<Object>();
			controllers.add(new TestController());
			return controllers;
		}
		@Override
		public ViewRenderer getDefaultViewRenderer() {
			return new TestViewRenderer();
		}
	}
	
	private class TestViewRenderer implements ViewRenderer {
		@Override
		public void render(ViewAndModel viewAndModel,
				HttpServletResponse response) {
			response.setStatus(HttpServletResponse.SC_OK); //Just to expect with EasyMock.
		}
	}
	
	public static class TestController {
		@RequestMapping("/test")
		public String test() {
			return "test";
		}
		
		@RequestMapping(value = "/post", requestMethod = RequestMethod.POST)
		public String post() {
			return "test";
		}
		
		@RequestMapping("/redirect")
		public String redirect() {
			return "redirect: /";
		}
		
		@RequestMapping("/existsbutthrows404")
		public String throw404() {
			throw new NotFoundException();
		}
		
		@RequestMapping("/existsbutthrows403")
		public String throw403() {
			throw new ForbiddenException();
		}
	}
}
