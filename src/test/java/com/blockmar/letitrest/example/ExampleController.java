package com.blockmar.letitrest.example;

import javax.servlet.http.HttpServletRequest;

import com.blockmar.letitrest.request.annotation.ParameterPojo;
import com.blockmar.letitrest.request.annotation.RequestMapping;
import com.blockmar.letitrest.views.ViewAndModel;
import com.blockmar.letitrest.views.json.JsonViewAndModel;

public class ExampleController {

	@RequestMapping("/")
	public ViewAndModel index() {
		return new ViewAndModel("index");
	}
	
	@RequestMapping("/name")
	public ViewAndModel simpleForm(HttpServletRequest request) {
		String name = request.getParameter("name");
		ViewAndModel viewAndModel = new ViewAndModel("name");
		viewAndModel.addAttribute("name", name);
		return viewAndModel;
	}
	
	@RequestMapping("/advanced/([a-z]+)")
	public ViewAndModel advancedForm(String urlParam, @ParameterPojo FormPojo form) {
		ViewAndModel viewAndModel = new ViewAndModel("name");
		viewAndModel.addAttribute("name", form.getName());
		viewAndModel.addAttribute("age", form.getAge());
		return viewAndModel;
	}
	
	@RequestMapping("/page/([0-9]+)")
	public ViewAndModel urlParamAsNumber(Integer page) {
		ViewAndModel viewAndModel = new ViewAndModel("pageNumber");
		viewAndModel.addAttribute("page", page);
		return viewAndModel;
	}	
	
	@RequestMapping("/page/([0-9]+)/subpage/([0-9]+)")
	public ViewAndModel pageNumberSub(String page, String subpage) {
		ViewAndModel viewAndModel = new ViewAndModel("pageNumber");
		viewAndModel.addAttribute("page", page);
		viewAndModel.addAttribute("subpage", subpage);
		return viewAndModel;
	}
	
	@RequestMapping("/json")
	public ViewAndModel jsonResponse() {
		ViewAndModel viewAndModel = new JsonViewAndModel();
		viewAndModel.addAttribute("number", 1);
		viewAndModel.addAttribute("string", "string");
		return viewAndModel;
	}
}
