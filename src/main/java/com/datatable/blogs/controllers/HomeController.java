package com.datatable.blogs.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.datatable.blogs.modal.Blog;
import com.datatable.blogs.repository.BlogRepository;

@Controller
public class HomeController {

	@Autowired
	private BlogRepository blogRepository;

	@GetMapping("/")
	public String showHomePage(Model model) {
		
		System.out.println("hello");
		return "home"; // View name for home page
	}

	@GetMapping("/allblogs")
	public String showCreateBlogForm(Model model) {

		Blog blog = new Blog();
		model.addAttribute("blog", blog);
		return "blogdatatable";
	}

	

}
