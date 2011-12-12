package com.blockmar.letitrest.views;

import java.util.HashMap;
import java.util.Map;

public class ViewAndModel {

	public static final ViewRenderer DEFAULT_VIEW_RENDERER = null;
	
	private final String view;
	private final Map<String, Object> model;
	
	private ViewRenderer viewRenderer = DEFAULT_VIEW_RENDERER;
	
	public ViewAndModel(String view) {
		this.view = view;
		this.model = new HashMap<String, Object>();
	}
	
	public ViewAndModel(String view, Map<String, Object> model) {
		this.view = view;
		this.model = model;
	}
	
	public String getView() {
		return view;
	}

	public Map<String, Object> getModel() {
		//TODO Return unmodifiable Map
		return model;
	}

	public void addAttribute(String key, Object value) {
		model.put(key, value);
	}
	
	public void addAllAttributes(Map<String, Object> model) {
		model.putAll(model);
	}
	
	public void setViewRenderer(ViewRenderer viewRenderer) {
		this.viewRenderer = viewRenderer;
	}
	
	public ViewRenderer getViewRenderer() {
		return viewRenderer;
	}
}
