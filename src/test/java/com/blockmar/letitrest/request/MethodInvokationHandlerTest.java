package com.blockmar.letitrest.request;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Test;

import com.blockmar.letitrest.resolver.MethodInvokationRequest;
import com.blockmar.letitrest.views.ViewAndModel;

public class MethodInvokationHandlerTest {

	@Test
	public void invokingMethodWithoutArguments() throws Exception {
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		EasyMock.replay(request);

		MethodInvokationRequest invokationRequest = new MethodInvokationRequest(
				this, getMethod("noArguments"), new String[] {});

		MethodInvokationHandler invokationHandler = new MethodInvokationHandler();
		ViewAndModel viewAndModel = invokationHandler.invoke(invokationRequest,
				request);
		assertNotNull(viewAndModel);
	}

	@Test
	public void invokingMethodWithStringArguments() throws Exception {
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		EasyMock.replay(request);

		MethodInvokationRequest invokationRequest = new MethodInvokationRequest(
				this, getMethod("stringArguments"), new String[] { "a", "b" });

		MethodInvokationHandler invokationHandler = new MethodInvokationHandler();
		ViewAndModel viewAndModel = invokationHandler.invoke(invokationRequest,
				request);
		assertNotNull(viewAndModel);
	}
	
	@Test
	public void invokingMethodWithMixedArguments() throws Exception {
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		EasyMock.replay(request);

		MethodInvokationRequest invokationRequest = new MethodInvokationRequest(
				this, getMethod("mixedArguments"), new String[] { "1", "b" });

		MethodInvokationHandler invokationHandler = new MethodInvokationHandler();
		ViewAndModel viewAndModel = invokationHandler.invoke(invokationRequest,
				request);
		assertNotNull(viewAndModel);
	}
	
	@Test
	public void invokingMethodWithStringReturnType() throws Exception {
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		EasyMock.replay(request);

		MethodInvokationRequest invokationRequest = new MethodInvokationRequest(
				this, getMethod("stringReturnType"), new String[] {});

		MethodInvokationHandler invokationHandler = new MethodInvokationHandler();
		ViewAndModel viewAndModel = invokationHandler.invoke(invokationRequest,
				request);
		assertNotNull(viewAndModel);
	}

	@Test(expected=IllegalArgumentException.class)
	public void invokingMethodWithIncorrectParamsThrowsException() throws Exception {
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		EasyMock.replay(request);

		MethodInvokationRequest invokationRequest = new MethodInvokationRequest(
				this, getMethod("invalidMethod"), new String[] {});

		MethodInvokationHandler invokationHandler = new MethodInvokationHandler();
		ViewAndModel viewAndModel = invokationHandler.invoke(invokationRequest,
				request);
		assertNotNull(viewAndModel);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void invokingMethodWithIncorrectParamsThrowsException2() throws Exception {
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		EasyMock.replay(request);

		MethodInvokationRequest invokationRequest = new MethodInvokationRequest(
				this, getMethod("invalidMethod"), new String[] { "a", "b" });

		MethodInvokationHandler invokationHandler = new MethodInvokationHandler();
		ViewAndModel viewAndModel = invokationHandler.invoke(invokationRequest,
				request);
		assertNotNull(viewAndModel);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void mappingStringToIntegerThrowsException() throws Exception {
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		EasyMock.replay(request);

		MethodInvokationRequest invokationRequest = new MethodInvokationRequest(
				this, getMethod("mixedArguments"), new String[] { "a", "b" });

		MethodInvokationHandler invokationHandler = new MethodInvokationHandler();
		ViewAndModel viewAndModel = invokationHandler.invoke(invokationRequest,
				request);
		assertNotNull(viewAndModel);
	}
	
	private Method getMethod(String methodName) {
		//Finds methosd regardless of parameters
		try {
			Method[] methods = this.getClass().getMethods();
			for (Method method : methods) {
				if(method.getName().equals(methodName)) {
					return method;
				}
			}
			throw new IllegalArgumentException("Method not found!");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ViewAndModel noArguments() {
		return new ViewAndModel("");
	}

	public ViewAndModel stringArguments(String a, String b) {
		return new ViewAndModel("");
	}
	
	public ViewAndModel mixedArguments(Integer a, String b) {
		return new ViewAndModel("");
	}
	
	public ViewAndModel invalidMethod(String a) {
		return new ViewAndModel("");
	}
	
	public String stringReturnType() {
		return "";
	}
}
