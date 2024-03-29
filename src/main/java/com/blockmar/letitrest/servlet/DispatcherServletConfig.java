package com.blockmar.letitrest.servlet;

import java.util.Set;

import com.blockmar.letitrest.request.RequestHandler;
import com.blockmar.letitrest.resolver.UrlResolver;
import com.blockmar.letitrest.resolver.regexp.PatternUrlResolver;
import com.blockmar.letitrest.views.JsonRenderer;
import com.blockmar.letitrest.views.ViewRenderer;
import com.blockmar.letitrest.views.freemarker.FreemarkerViewRenderer;
import com.blockmar.letitrest.views.json.JacksonJsonRenderer;
import com.blockmar.letitrest.views.redirect.RedirectViewRenderer;

public abstract class DispatcherServletConfig {

	public abstract Set<Object> getControllers();

	public UrlResolver getUrlResolver() {
		return new PatternUrlResolver();
	}

	public ViewRenderer getDefaultViewRenderer() {
		return new FreemarkerViewRenderer();
	}
	
	public ViewRenderer getRedirectViewRenderer() {
		return new RedirectViewRenderer();
	}

	public JsonRenderer getDefaultJsonRenderer() {
		return new JacksonJsonRenderer();
	}

	public RequestHandler getRequestHandler() {
		return new RequestHandler(this);
	}

}
