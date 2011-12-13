package com.blockmar.letitrest.views.freemarker;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.MimeTypes;

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

		String viewName = viewAndModel.getView();
		Writer writer = new StringWriter(1500);

		try {
			Template template = freeMarkerConfiguration.getTemplate(viewName
					+ ".ftl");
			template.process(viewAndModel.getModel(), writer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (TemplateException e) {
			throw new RuntimeException(e);
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MimeTypes.TEXT_HTML_UTF_8);

		try {
			writer.flush();
			
			String content = writer.toString();
			
			response.setContentLength(content.length());
			OutputStream out = response.getOutputStream();
			out.write(content.getBytes());
			out.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
