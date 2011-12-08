package com.blockmar.letitrest.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final DispatcherServletConfig servletConfig;

	public DispatcherServlet(DispatcherServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("Hello, world!");
		out.close();
	}
}
