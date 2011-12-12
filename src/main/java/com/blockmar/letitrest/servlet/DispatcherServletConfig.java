package com.blockmar.letitrest.servlet;

import java.util.Set;

import com.blockmar.letitrest.resolver.UrlResolver;
import com.blockmar.letitrest.resolver.impl.PatternUrlResolver;
import com.blockmar.letitrest.views.ViewRenderer;
import com.blockmar.letitrest.views.impl.FreemarkerViewRenderer;
import com.blockmar.letitrest.views.impl.RedirectViewRenderer;

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

}
