package com.blockmar.letitrest.resolver;

import java.lang.reflect.Method;

import org.easymock.EasyMock;
import org.junit.Test;

import com.blockmar.letitrest.request.RequestMethod;
import com.blockmar.letitrest.request.annotation.FallbackRequestMapping;
import com.blockmar.letitrest.request.annotation.RequestMapping;

public class AnnotationScannerTest {
	@Test
	public void scannerAddsRequestMappingAnnotatedMethod() throws Exception {
		UrlResolver urlResolver = EasyMock.createMock(UrlResolver.class);
		TestController1 controller = new TestController1();
		Method method = controller.getClass().getMethod("test");
		urlResolver.registerUrl("/test", controller, method, RequestMethod.GET);
		EasyMock.replay(urlResolver);
		
		AnnotationScanner scanner = new AnnotationScanner(urlResolver);
		scanner.scan(controller);
		
		EasyMock.verify(urlResolver);
	}
	
	@Test
	public void scannerAddsFallbackRequestMappingAnnotatedMethod() throws Exception {
		UrlResolver urlResolver = EasyMock.createMock(UrlResolver.class);
		TestController2 controller = new TestController2();
		Method method = controller.getClass().getMethod("test");
		urlResolver.registerFallbackUrl(controller, method, RequestMethod.GET);
		EasyMock.replay(urlResolver);
		
		AnnotationScanner scanner = new AnnotationScanner(urlResolver);
		scanner.scan(controller);
		
		EasyMock.verify(urlResolver);
	}
	
	public static class TestController1 {
		@RequestMapping(value = "/test", requestMethod = RequestMethod.GET)
		public String test() {
			return "test";
		}		
	}
	
	public static class TestController2 {
		@FallbackRequestMapping(requestMethod = RequestMethod.GET)
		public String test() {
			return "test";
		}		
	}
}
