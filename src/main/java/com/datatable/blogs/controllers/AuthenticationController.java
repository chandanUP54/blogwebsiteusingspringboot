package com.datatable.blogs.controllers;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.datatable.blogs.dto.SignInRequest;
import com.datatable.blogs.dto.SignUpRequest;
import com.datatable.blogs.exception.UserException;
import com.datatable.blogs.modal.Users;
import com.datatable.blogs.repository.UserRepository;
import com.datatable.blogs.response.JwtAuthenticationResponse;
import com.datatable.blogs.userservices.AuthenticationService;

import jakarta.servlet.http.*;

//@RestController
@Controller
@RequestMapping
//@CrossOrigin("*")
public class AuthenticationController {

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	// work on controller,Thymeleaf,ModelAttribute
	@PostMapping("/signup")
	public String signup(@ModelAttribute SignUpRequest signUpRequest, Model model) {
		try {

			System.out.println("signupreq->" + signUpRequest);

			Users newUser = authenticationService.signup(signUpRequest);

			model.addAttribute("successMessage", "Signup successful! Please log in.");
			return "redirect:/signin";
		} catch (UserException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "signup";
		}
	}


	@PostMapping("/signin")
	public String login(@ModelAttribute SignInRequest signInRequest, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("signin: " + signInRequest);

		Optional<Users> userOptional = userRepository.findByEmail(signInRequest.getUsername());

		if (userOptional.isPresent()
				&& passwordEncoder.matches(signInRequest.getPassword(), userOptional.get().getPassword())) {
			Users user = userOptional.get();

			Collection<GrantedAuthority> authorities = user.getRoles().stream()
					.map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

			// Create Authentication object
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
					authorities);

			SecurityContextHolder.getContext().setAuthentication(authToken); // Set authentication in context

			JwtAuthenticationResponse jwtResponse = authenticationService.signin(signInRequest);
			createJwtCookies(response, jwtResponse); // Create and set cookies

			HttpSession session = request.getSession(true); // true creates a new session if none exists

			session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
					SecurityContextHolder.getContext());

			return "redirect:/"; // Redirect on successful login
		}

		model.addAttribute("error", "Invalid username or password");
		return "signin"; // Return to signin page if authentication fails
	}

	// Helper method to create JWT cookies
	private void createJwtCookies(HttpServletResponse response, JwtAuthenticationResponse jwtResponse) {
		Cookie tokenCookie = new Cookie("token", jwtResponse.getToken());
		tokenCookie.setMaxAge(60 * 60 * 24); // 1 day
		tokenCookie.setPath("/");
		tokenCookie.setSecure(true);
		response.addCookie(tokenCookie);

		Cookie refreshTokenCookie = new Cookie("refreshToken", jwtResponse.getRefreshToken());
		refreshTokenCookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setSecure(true);
		response.addCookie(refreshTokenCookie);

	}

	@GetMapping("/x") //for custom logout
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		// Invalidate the session
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		SecurityContextHolder.clearContext();
        //revoke all jwt also
		clearCookies(request, response, "JSESSIONID", "token", "refreshToken");

		return "redirect:/signin?logout=true";
	}

	private void clearCookies(HttpServletRequest request, HttpServletResponse response, String... cookiesToDelete) {
		for (String cookieName : cookiesToDelete) {
			Cookie cookie = new Cookie(cookieName, null);
			cookie.setPath(request.getContextPath());
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}

}
