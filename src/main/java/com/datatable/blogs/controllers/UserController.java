package com.datatable.blogs.controllers;

import java.security.Principal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.datatable.blogs.modal.Users;
import com.datatable.blogs.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/user")
	public String getUser(Principal p, Model m, HttpServletRequest request) {

		Optional<Users> user = userRepository.findByEmail(p.getName());

		if (user.isPresent()) {
			m.addAttribute("user", user.get());
			m.addAttribute("role", user.get().getRoles().iterator().next().getName());
		}


		String t = null;
		if(request.getCookies()!=null)
		{
			Cookie[]rc =request.getCookies();
			
			for(int i=0;i<rc.length;i++)
			{
				if(rc[i].getName().equals("token")==true)
				{
					
				t = rc[i].getValue().toString();
				}
			}
		}
		
		
	String requestTokenHeader = "Bearer "+t;
	
	System.out.println("request token:-> "+requestTokenHeader);
		
		return "user";
	}
}
