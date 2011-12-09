package com.blockmar.letitrest.example;

import java.util.HashSet;
import java.util.Set;

import com.blockmar.letitrest.servlet.DispatcherServletConfig;

public class ExampleConfig extends DispatcherServletConfig {

	public Set<Object> getControllers() {
		Set<Object> contrllers = new HashSet<Object>();
		contrllers.add(new ExampleController());
		return contrllers;
	}

}
