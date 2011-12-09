package com.blockmar.letitrest.views;

import javax.servlet.http.HttpServletResponse;

public interface ViewRenderer {
	public void render(ViewAndModel viewAndModel, HttpServletResponse response);
}
