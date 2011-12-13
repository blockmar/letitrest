package com.blockmar.letitrest.views.redirect;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.blockmar.letitrest.views.ViewAndModel;
import com.blockmar.letitrest.views.ViewRenderer;

public class RedirectViewRenderer implements ViewRenderer {

	@Override
	public void render(ViewAndModel viewAndModel, HttpServletResponse response) {
		try {
			response.sendRedirect(viewAndModel.getView());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
