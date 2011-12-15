package com.blockmar.letitrest.views.json;

import com.blockmar.letitrest.views.ViewAndModel;

public class JsonViewAndModel extends ViewAndModel {	
	public JsonViewAndModel() {
		super(null);
	}
	
	@Override
	public String getView() {
		throw new UnsupportedOperationException("View not used for Json responses");
	}
}
