package com.blockmar.letitrest.views.json;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;

import com.blockmar.letitrest.views.JsonRenderer;

public class JacksonJsonRenderer implements JsonRenderer {

	private static final String MIME_TYPE = "application/json;charset=UTF-8";

	private final ObjectMapper mapper;

	public JacksonJsonRenderer() {
		mapper = new ObjectMapper();
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, true);
	}

	@Override
	public void render(Object objectToRender, HttpServletResponse response) {
		try {
			String json = mapper.writeValueAsString(objectToRender);

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(MIME_TYPE);

			response.setContentLength(json.length());
			OutputStream out = response.getOutputStream();
			out.write(json.getBytes());
			out.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
