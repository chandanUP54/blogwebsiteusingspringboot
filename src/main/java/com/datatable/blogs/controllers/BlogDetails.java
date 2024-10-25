package com.datatable.blogs.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.datatable.blogs.modal.Blog;
import com.datatable.blogs.services.BlogService;

@Controller
public class BlogDetails {

	@Autowired
	private BlogService blogService;
	
	//use templating here
	@GetMapping("/blog/{id}")
	public String blogDetailById(@PathVariable long id,Model model){
			Blog blog=blogService.getBlogById(id);
			model.addAttribute("blog",blog);
			return "blogpage";
		
	}
	
	
	
	
}

