package com.datatable.blogs.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.datatable.blogs.modal.Blog;
import com.datatable.blogs.services.BlogService;

@Controller
public class HomeController {

	@Autowired
	private BlogService blogService;

	@GetMapping("/")
	public String showHomePage(Model model) {

		List<Blog> recentBlogs = blogService.getRecentBlogs();

		model.addAttribute("recentBlogs", recentBlogs);

		return "home"; // View name for home page
	}

	@GetMapping("/allblogs")
	public String showCreateBlogForm(Model model) {

		Blog blog = new Blog();
		model.addAttribute("blog", blog);
		return "blogdatatable";
	}

	@GetMapping("/404")
	public String handle404() {
		return "error";
	}
}
