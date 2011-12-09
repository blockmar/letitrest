package com.blockmar.letitrest.example;

import com.blockmar.letitrest.servlet.RequestMapping;
import com.blockmar.letitrest.views.ViewAndModel;

public class ExampleController {

	@RequestMapping("/")
	public ViewAndModel index() {
		return new ViewAndModel("index");
	}
	
// Not implemented	
//	@RequestMapping("/name")
//	public ViewAndModel yourName(String name) {
//		ViewAndModel viewAndModel = new ViewAndModel("name");
//		viewAndModel.addAttribute("name", name);
//		return viewAndModel;
//	}
	
	@RequestMapping("/page/([0-9]+)")
	public ViewAndModel pageNumber(String page) {
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
}
