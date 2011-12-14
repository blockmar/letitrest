package com.blockmar.letitrest.views.json;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Test;

import com.blockmar.letitrest.views.ViewAndModel;
import com.blockmar.letitrest.views.ViewRenderer;

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

		ViewAndModel viewAndModel = new ViewAndModel("json");
		viewAndModel.addAttribute("key", "value");

		ViewRenderer viewRenderer = new JacksonViewRenderer();
		viewRenderer.render(viewAndModel, response);
		
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
		
		public String toString() {
			return outputStream.toString();
		}
	}
}
