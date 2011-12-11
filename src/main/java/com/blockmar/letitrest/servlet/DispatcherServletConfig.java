package com.blockmar.letitrest.servlet;

import java.util.Set;

import com.blockmar.letitrest.resolver.UrlResolver;
import com.blockmar.letitrest.resolver.impl.PatternUrlResolver;
import com.blockmar.letitrest.views.ViewRenderer;
import com.blockmar.letitrest.views.impl.FreemarkerViewRenderer;

public abstract class DispatcherServletConfig {

	public abstract Set<Object> getControllers();

	public UrlResolver getUrlResolver() {
		return new PatternUrlResolver();
	}

	public ViewRenderer getViewRenderer() {
		return new FreemarkerViewRenderer();
	}

}
