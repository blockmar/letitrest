package com.blockmar.letitrest.views.impl;

import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.util.ByteArrayISO8859Writer;

import com.blockmar.letitrest.views.ViewAndModel;
import com.blockmar.letitrest.views.ViewRenderer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerViewRenderer implements ViewRenderer {

	private final Configuration freeMarkerConfiguration;
	
	public FreemarkerViewRenderer() {
		this.freeMarkerConfiguration = new FreemarkerConfiguration();
	}

	@Inject
	public FreemarkerViewRenderer(Configuration freeMarkerConfiguration) {
		this.freeMarkerConfiguration = freeMarkerConfiguration;
	}
	
	@Override
	public void render(ViewAndModel viewAndModel, HttpServletResponse response) {
		
		ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer(1500);
		
		String viewName = viewAndModel.getView();
		
		try {
			Template template = freeMarkerConfiguration
					.getTemplate(viewName + ".ftl");
			template.process(viewAndModel.getModel(), writer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (TemplateException e) {
			throw new RuntimeException(e);
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MimeTypes.TEXT_HTML_8859_1);
		
		writer.flush();

		try {
			response.setContentLength(writer.size());
			OutputStream out = response.getOutputStream();
			writer.writeTo(out);
			out.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}	
}
