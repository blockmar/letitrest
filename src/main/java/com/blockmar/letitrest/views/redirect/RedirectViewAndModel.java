package com.blockmar.letitrest.views.redirect;

import java.util.Map;

import com.blockmar.letitrest.views.ViewAndModel;

public class RedirectViewAndModel extends ViewAndModel {	
	public RedirectViewAndModel(String redirectUrl) {
		super(redirectUrl);
	}
	
	@Override
	public Map<String, Object> getModel() {
		throw new UnsupportedOperationException("Model not used for Redirect responses");
	}
}
