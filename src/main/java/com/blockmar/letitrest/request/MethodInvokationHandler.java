package com.blockmar.letitrest.request;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import com.blockmar.letitrest.request.annotation.ParameterPojo;
import com.blockmar.letitrest.resolver.MethodInvokationRequest;
import com.blockmar.letitrest.views.ViewAndModel;
import com.blockmar.letitrest.views.redirect.Redirect;

public class MethodInvokationHandler {

	private static final String REDIRECT_PREFIX = "redirect:";

	private final ReflectionPojoMapper pojoMapper;

	public MethodInvokationHandler() {
		pojoMapper = new ReflectionPojoMapper();
	}

	public ViewAndModel invoke(MethodInvokationRequest urlHandler,
			HttpServletRequest request) {

		try {
			Object methodResult = invokeMethod(urlHandler, request);

			if (methodResult instanceof ViewAndModel) {
				return (ViewAndModel) methodResult;
			} else if (methodResult instanceof String) {
				String methodResultString = (String) methodResult;
				if (!methodResultString.startsWith(REDIRECT_PREFIX)) {
					return new ViewAndModel(methodResultString);
				} else {
					return new Redirect(stripRedirectPrefix(methodResultString));
				}
			} else {
				throw new UnsupportedOperationException(
						"Can't handle methods with return type: "
								+ urlHandler.getMethod().getReturnType()
										.toString());
			}

		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new RuntimeException(e);
		} catch (RuntimeException e) {
			throw e; // Not to wrap RuntimeExceptions again
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String stripRedirectPrefix(String redirect) {
		return redirect.substring(REDIRECT_PREFIX.length()).trim();
	}

	private Object invokeMethod(MethodInvokationRequest urlHandler,
			HttpServletRequest request) throws IllegalAccessException,
			InvocationTargetException {

		Object methodResult;
		Method method = urlHandler.getMethod();
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (hasNoParameters(parameterTypes)) {
			methodResult = method.invoke(urlHandler.getInstance());
		} else if (hasHttpRequestAsParameter(parameterTypes)) {
			methodResult = method.invoke(urlHandler.getInstance(), request);
		} else {
			String[] urlParams = urlHandler.getUrlParameters();
			Object[] methodParameters = prepareParameters(method, request,
					urlParams);
			methodResult = method.invoke(urlHandler.getInstance(),
					methodParameters);
		}
		return methodResult;
	}

	private Object[] prepareParameters(Method method,
			HttpServletRequest request, String[] urlParams) {

		Class<?>[] parameterTypes = method.getParameterTypes();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		Object[] result = new Object[parameterTypes.length];

		int urlParamCount = 0;
		try {
			for (int position = 0; position < parameterTypes.length; position++) {
				Class<?> parameterType = parameterTypes[position];

				if (parameterType.isAssignableFrom(String.class)) {
					result[position] = urlParams[urlParamCount++];
				} else if (parameterType.isAssignableFrom(Integer.class)) {
					String value = urlParams[urlParamCount++];
					try {
						result[position] = new Integer(value);
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException(
								"Failed to cast parameter value to Integer: "
										+ value);
					}
				} else if (parameterType.isAssignableFrom(Double.class)) {
					String value = urlParams[urlParamCount++];
					try {
						result[position] = new Double(value);
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException(
								"Failed to cast parameter value to Integer: "
										+ value);
					}
				} else if (isPojoParameter(parameterAnnotations[position])) {
					result[position] = populatePojo(parameterType, request);
				} else {
					throw new IllegalArgumentException(
							"Parameter type not supported: "
									+ parameterType.getName());
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException(
					"Missmatched number of parameters in method signature");
		}

		if (urlParamCount != urlParams.length) {
			throw new IllegalArgumentException(
					"Missmatched number of parameters in method signature");
		}

		return result;
	}

	private Object populatePojo(Class<?> pojoClass, HttpServletRequest request) {
		return pojoMapper.map(pojoClass, request);
	}

	private boolean isPojoParameter(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().isAssignableFrom(
					ParameterPojo.class)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasNoParameters(Class<?>[] parameterTypes) {
		return parameterTypes.length == 0;
	}

	private boolean hasHttpRequestAsParameter(Class<?>[] parameterTypes) {
		return parameterTypes.length == 1
				&& parameterTypes[0].equals(HttpServletRequest.class);
	}

}
