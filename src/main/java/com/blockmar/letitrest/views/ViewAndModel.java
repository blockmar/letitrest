package com.blockmar.letitrest.views;

import java.util.HashMap;
import java.util.Map;

public class ViewAndModel {

	private final String view;
	private final Map<String, Object> model = new HashMap<String, Object>();
	
	public ViewAndModel(String view) {
		this.view = view;
	}
	
	public String getView() {
		return view;
	}

	public Map<String, Object> getModel() {
		return model;
	}

	public void addAttribute(String key, Object value) {
		model.put(key, value);
	}
	
	public void addAllAttributes(Map<String, Object> model) {
		model.putAll(model);
	}
}
