package com.blockmar.letitrest.views.json;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Test;

import com.blockmar.letitrest.views.JsonRenderer;

public class JacksonViewRendererTest {
	@Test
	public void jacksonViewRendererReturnModelAsJSON() throws Exception {
		HttpServletResponse response = EasyMock
				.createMock(HttpServletResponse.class);		
		ServletOutputStream output = new TestServletOutputStream();
		
		response.setStatus(200);
		response.setContentType("application/json;charset=UTF-8");
		response.setContentLength(EasyMock.anyInt());
		EasyMock.expect(response.getOutputStream()).andReturn(output);
		EasyMock.replay(response);

		Map<String, Object> json = new HashMap<String, Object>();
		json.put("key", "value");

		JsonRenderer renderer = new JacksonJsonRenderer();
		renderer.render(json, response);
		
		assertEquals("{\"key\":\"value\"}", output.toString());
	}
	
	private class TestServletOutputStream extends ServletOutputStream {

		private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();;
		
		public TestServletOutputStream() {
		}
			
		@Override
		public void write(int b) throws IOException {
			outputStream.write(b);
		}
		
		@Override
		public String toString() {
			return outputStream.toString();
		}
	}
}
