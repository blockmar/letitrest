package com.blockmar.letitrest.servlet;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Test;

import com.blockmar.letitrest.views.ViewAndModel;
import com.blockmar.letitrest.views.ViewRenderer;

public class DispatcherServletTest {

	@Test
	public void redirectStringPrefixSendsRedirect() throws Exception {
		TestDispatcherServlet servletConfig = new TestDispatcherServlet();
		DispatcherServlet dispatcherServlet = new DispatcherServlet(servletConfig);
		
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getRequestURI()).andReturn("/redirect");		
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		response.sendRedirect("/");
		
		EasyMock.replay(request, response);
		
		dispatcherServlet.doGet(request, response);
		EasyMock.verify(request, response);
	}
	
	@Test
	public void standardUrlsUseDefaultRenderer() throws Exception {
		TestDispatcherServlet servletConfig = new TestDispatcherServlet();
		DispatcherServlet dispatcherServlet = new DispatcherServlet(servletConfig);
		
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getRequestURI()).andReturn("/test");		
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		response.setStatus(HttpServletResponse.SC_OK);
		
		EasyMock.replay(request, response);
		
		dispatcherServlet.doGet(request, response);
		EasyMock.verify(request, response);
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
		
		@RequestMapping("/redirect")
		public String redirect() {
			return "redirect: /";
		}
	}
}
