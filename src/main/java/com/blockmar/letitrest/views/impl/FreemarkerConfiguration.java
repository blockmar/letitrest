package com.blockmar.letitrest.views.impl;

import java.util.Locale;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;

public class FreemarkerConfiguration extends Configuration {
	
	private static final String TEMPLATE_CLASSPATH = "/freemarker/";

	public FreemarkerConfiguration() {
		setTemplateLoader(configurationTemplateLoader());
		setLocale(Locale.US);
	}

	protected TemplateLoader configurationTemplateLoader() {
		return new ClassTemplateLoader(FreemarkerConfiguration.class,
				TEMPLATE_CLASSPATH);
	}
}
