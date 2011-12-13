package com.blockmar.letitrest.views.redirect;

import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Test;

import com.blockmar.letitrest.views.ViewAndModel;
import com.blockmar.letitrest.views.ViewRenderer;

public class RedirectViewRendererTest {
	private static final String VIEWNAME = "viewname";

	@Test
	public void rendererCallsSendRedirectWithViewNameAsUrl() throws Exception {	
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		response.sendRedirect(VIEWNAME);
		EasyMock.replay(response);
		
		ViewRenderer renderer = new RedirectViewRenderer();
		ViewAndModel viewAndModel = new ViewAndModel(VIEWNAME);
		
		renderer.render(viewAndModel, response);
		EasyMock.verify(response);
	}
}
