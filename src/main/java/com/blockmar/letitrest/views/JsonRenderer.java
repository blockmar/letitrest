package com.blockmar.letitrest.views;

import javax.servlet.http.HttpServletResponse;

public interface JsonRenderer {
	public void render(Object objectToRender, HttpServletResponse response);
}
