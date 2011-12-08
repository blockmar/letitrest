package com.blockmar.letitrest.example;

import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.blockmar.letitrest.servlet.DispatcherServlet;
import com.blockmar.letitrest.servlet.DispatcherServletConfig;

public class HttpServer {
	
	private static final String HOSTNAME = "localhost";
	private static final int POST = 8080;

	public static void main(String[] args) {
		
		DispatcherServletConfig config = new ExampleConfig();
		DispatcherServlet servlet = new DispatcherServlet(config);
		
		InetSocketAddress  addr = new InetSocketAddress(HOSTNAME, POST);
		Server server = new Server(addr);
		
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
 
        context.addServlet(new ServletHolder(servlet),"/*");
        
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
