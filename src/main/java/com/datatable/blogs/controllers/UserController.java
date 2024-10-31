package com.datatable.blogs.controllers;

import java.security.Principal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.datatable.blogs.modal.Users;
import com.datatable.blogs.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class UserController {

	
	@Autowired
	private UserRepository userRepository;

	@GetMapping
	public String getUser(Principal p, Model m, HttpServletRequest request) {

		String users = p.getName();
		String email = users.substring(users.indexOf("email=") + 6, users.indexOf(",", users.indexOf("email=")));

		Optional<Users> user = userRepository.findByEmail(email);

		if (user.isPresent()) {
			m.addAttribute("user", user.get());
			m.addAttribute("role", user.get().getRoles().iterator().next().getName());
		}

		String t = null;
		if (request.getCookies() != null) {
			Cookie[] rc = request.getCookies();

			for (int i = 0; i < rc.length; i++) {
				if (rc[i].getName().equals("token") == true) {

					t = rc[i].getValue().toString();
				}
			}
		}

		String requestTokenHeader = "Bearer " + t;

		System.out.println("requestx token:-> " + requestTokenHeader);

		return "user";
	}
	

}
