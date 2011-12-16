package com.blockmar.letitrest.example;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.blockmar.letitrest.request.annotation.JsonResponse;
import com.blockmar.letitrest.request.annotation.ParameterPojo;
import com.blockmar.letitrest.request.annotation.RequestMapping;
import com.blockmar.letitrest.views.ViewAndModel;

public class ExampleController {

	@RequestMapping("/")
	public ViewAndModel index() {
		return new ViewAndModel("index");
	}
	
	@RequestMapping("/name")
	public ViewAndModel simpleForm(HttpServletRequest request) {
		String name = request.getParameter("name");
		String age = request.getParameter("age");
		ViewAndModel viewAndModel = new ViewAndModel("name");
		viewAndModel.addAttribute("name", name);
		viewAndModel.addAttribute("age", age);
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
	@JsonResponse
	public Map<String, Object> jsonResponse() {
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("number", 1);
		response.put("string", "abcd");
		return response;
	}
}
