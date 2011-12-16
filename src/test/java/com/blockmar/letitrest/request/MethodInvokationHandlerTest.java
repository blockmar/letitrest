package com.blockmar.letitrest.request;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Test;

import com.blockmar.letitrest.request.annotation.JsonResponse;
import com.blockmar.letitrest.request.annotation.ParameterPojo;
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
		Object viewAndModel = invokationHandler.invoke(invokationRequest,
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
		Object viewAndModel = invokationHandler.invoke(invokationRequest,
				request);
		assertNotNull(viewAndModel);
	}
	
	@Test
	public void invokingMethodWithMixedArguments() throws Exception {
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		EasyMock.replay(request);

		MethodInvokationRequest invokationRequest = new MethodInvokationRequest(
				this, getMethod("mixedArguments"), new String[] { "1", "b", "2.0" });

		MethodInvokationHandler invokationHandler = new MethodInvokationHandler();
		Object viewAndModel = invokationHandler.invoke(invokationRequest,
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
		Object viewAndModel = invokationHandler.invoke(invokationRequest,
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
		invokationHandler.invoke(invokationRequest,
				request);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void invokingMethodWithIncorrectParamsThrowsException2() throws Exception {
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		EasyMock.replay(request);

		MethodInvokationRequest invokationRequest = new MethodInvokationRequest(
				this, getMethod("invalidMethod"), new String[] { "a", "b" });

		MethodInvokationHandler invokationHandler = new MethodInvokationHandler();
		invokationHandler.invoke(invokationRequest,
				request);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void mappingStringToIntegerThrowsException() throws Exception {
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		EasyMock.replay(request);

		MethodInvokationRequest invokationRequest = new MethodInvokationRequest(
				this, getMethod("mixedArguments"), new String[] { "a", "b" });

		MethodInvokationHandler invokationHandler = new MethodInvokationHandler();
		invokationHandler.invoke(invokationRequest,
				request);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void invokingMethodWithInvalidParameterTypeThrowsException() throws Exception {
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		EasyMock.replay(request);

		MethodInvokationRequest invokationRequest = new MethodInvokationRequest(
				this, getMethod("invalidParameterType"), new String[] { "b" });

		MethodInvokationHandler invokationHandler = new MethodInvokationHandler();
		invokationHandler.invoke(invokationRequest,
				request);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void invokingMethodWithUnknownReturnTypeAndNotJsonThrowsException()
			throws Exception {
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		EasyMock.replay(request);

		MethodInvokationRequest invokationRequest = new MethodInvokationRequest(
				this, getMethod("notJson"), new String[] {});

		MethodInvokationHandler invokationHandler = new MethodInvokationHandler();
		invokationHandler.invoke(invokationRequest,
				request);
	}

	@Test
	public void invokingMethodJsonAnnotationReturnsJsonData() throws Exception {
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		EasyMock.replay(request);

		MethodInvokationRequest invokationRequest = new MethodInvokationRequest(
				this, getMethod("asJson"), new String[] {});

		MethodInvokationHandler invokationHandler = new MethodInvokationHandler();
		Object json = invokationHandler.invoke(invokationRequest,
				request);
		assertNotNull(json);
	}

	@Test
	public void invokingMethodWithRequestPojo() throws Exception {	
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getParameterMap()).andReturn(new HashMap<String, String[]>());
		EasyMock.replay(request);

		MethodInvokationRequest invokationRequest = new MethodInvokationRequest(
				this, getMethod("requestPojo"), new String[] { "b" });

		MethodInvokationHandler invokationHandler = new MethodInvokationHandler();
		Object viewAndModel = invokationHandler.invoke(invokationRequest,
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
		assertNotNull(a);
		assertNotNull(b);
		return new ViewAndModel("");
	}
	
	public ViewAndModel mixedArguments(Integer a, String b, Double c) {
		assertNotNull(a);
		assertNotNull(b);
		return new ViewAndModel("");
	}
	
	public ViewAndModel invalidMethod(String a) {
		return new ViewAndModel("");
	}
	
	public ViewAndModel invalidParameterType(Date date) {
		return new ViewAndModel("");
	}
	
	public ViewAndModel requestPojo(String a, @ParameterPojo Pojo pojo) {
		assertNotNull(pojo);
		return new ViewAndModel("");
	}

	public Object notJson() {
		return null;
	}
	
	@JsonResponse
	public Object[] asJson() {
		return new Object[] { "key", 1 };
	}

	public String stringReturnType() {
		return "";
	}
	
	public static class Pojo {		
	}
}
