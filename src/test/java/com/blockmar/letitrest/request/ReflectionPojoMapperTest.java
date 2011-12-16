package com.blockmar.letitrest.request;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Test;

public class ReflectionPojoMapperTest {
	
	@Test
	public void mapperReturnsFilledObject() throws Exception {
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		HashMap<String, String[]> parameterMap = new HashMap<String, String[]>();
		parameterMap.put("age", new String[]{ "1" });
		parameterMap.put("size", new String[]{ "2.0" });
		parameterMap.put("name", new String[]{ "aaa" });
		parameterMap.put("nomethod", new String[]{ "1234" });
		EasyMock.expect(request.getParameterMap()).andReturn(
				parameterMap);
		EasyMock.replay(request);

		ReflectionPojoMapper mapper = new ReflectionPojoMapper();
		
		TestPojo mappedPojo = (TestPojo) mapper.map(TestPojo.class, request);
		
		assertEquals(new Integer(1), mappedPojo.getAge());
		assertEquals("aaa", mappedPojo.getName());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void parametersArrayNotAllowed() throws Exception {
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		HashMap<String, String[]> parameterMap = new HashMap<String, String[]>();
		parameterMap.put("age", new String[]{ "1", "2" });
		parameterMap.put("name", new String[]{ "aaa" });
		EasyMock.expect(request.getParameterMap()).andReturn(
				parameterMap);
		EasyMock.replay(request);

		ReflectionPojoMapper mapper = new ReflectionPojoMapper();
		
		mapper.map(TestPojo.class, request);
	}

	public static class TestPojo {
		private String name;
		private Integer age;
		private Double size;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}
		
		public Double getSize() {
			return size;
		}

		public void setSize(Double size) {
			this.size = size;
		}
	}
}
