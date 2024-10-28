package com.datatable.blogs.userservices;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		var authourities = authentication.getAuthorities();
		var role = authourities.stream().map(GrantedAuthority::getAuthority).findFirst();

		if (role.isPresent()) {
			switch (role.get()) {
			case "ROLE_ADMIN":
				response.sendRedirect("/admin");
				break;
			case "ROLE_USER":
				response.sendRedirect("/user");
				break;
			default:
				response.sendRedirect("/error");
				break;
			}
		} else {
			response.sendRedirect("/error");
		}

	}

	
}