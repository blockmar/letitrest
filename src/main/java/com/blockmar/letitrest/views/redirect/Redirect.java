package com.blockmar.letitrest.views.redirect;

import java.util.Map;

import com.blockmar.letitrest.views.ViewAndModel;

public class Redirect extends ViewAndModel {	
	public Redirect(String redirectUrl) {
		super(redirectUrl);
	}
	
	@Override
	public Map<String, Object> getModel() {
		throw new UnsupportedOperationException("Model not used for Redirect responses");
	}
}
