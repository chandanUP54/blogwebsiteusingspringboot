package com.datatable.blogs.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.datatable.blogs.dto.SignInRequest;
import com.datatable.blogs.dto.SignUpRequest;
import com.datatable.blogs.modal.Blog;
import com.datatable.blogs.modal.Users;
import com.datatable.blogs.services.BlogService;

@Controller
public class HomeController {

	@Autowired
	private BlogService blogService;

	@GetMapping("/")
	public String showHomePage(Model model, Principal principal) {

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

	@GetMapping("/admin")
	public String getAdmin(Model model) {

		return "admin";
	}

	@GetMapping("/signin")
	public String getSignin(Model model) {
		model.addAttribute("signInRequest", new SignInRequest());

		return "signin";
	}

	@GetMapping("/signup")
	public String getSignup(Model model) {
		model.addAttribute("signUpRequest", new SignUpRequest());

		return "signup";
	}

	@GetMapping("/about")
	public String getMapping() {
		SecurityContext context = SecurityContextHolder.getContext();
		System.out.println("Current Authentication: " + context.getAuthentication());
		return "about";
	}

	@GetMapping("/view/articles")
	public String getAllPublishedBlog(Model model) {

		List<Blog> blogs = blogService.findAllBlogs();

		model.addAttribute("blogs", blogs);

		return "publishedblog";
	}
}
