package com.blockmar.letitrest.request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ReflectionPojoMapper {

	public Object map(Class<?> pojoClass, HttpServletRequest request) {

		Object pojo;
		try {
			pojo = pojoClass.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Failed to create intance of pojo.", e);
		}

		Method[] avaliableMethods = pojoClass.getMethods();
		Map<String, String[]> parameterMap = request.getParameterMap();

		for (Method method : avaliableMethods) {
			String methodName = method.getName();
			if (includeMethod(methodName)) {
				String[] values = parameterMap.get(getKeyFromName(methodName));
				if (values != null) {
					if (values.length == 1) {
						callSetter(pojo, method, values[0]);
					} else {
						throw new IllegalArgumentException(
								"Can not map array request parameters.");
					}
				}
			}
		}

		return pojo;
	}

	private void callSetter(Object pojo, Method method, String value) {
		Class<?>[] parameterTypes = method.getParameterTypes();

		if (parameterTypes.length != 1) {
			throw new IllegalArgumentException("Unsupported setter method: "
					+ method.getName());
		}

		Class<?> parameterType = parameterTypes[0];

		Object parameter;
		if (parameterType.isAssignableFrom(String.class)) {
			parameter = value;
		} else if (parameterType.isAssignableFrom(Integer.class)) {
			try {
				parameter = new Integer(value);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(
						"Failed to cast parameter value to Integer: " + value);
			}
		} else if (parameterType.isAssignableFrom(Double.class)) {
			try {
				parameter = new Double(value);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(
						"Failed to cast parameter value to Integer: " + value);
			}
		} else {
			throw new IllegalArgumentException("Parameter type not supported: "
					+ parameterType.getName());
		}

		try {
			method.invoke(pojo, parameter);
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

	private Object getKeyFromName(String methodName) {
		return methodName.substring(3).toLowerCase();
	}

	private boolean includeMethod(String methodName) {
		return methodName.length() > 3 && methodName.startsWith("set");
	}
}
