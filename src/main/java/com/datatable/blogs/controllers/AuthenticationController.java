package com.datatable.blogs.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datatable.blogs.dto.SignInRequest;
import com.datatable.blogs.dto.SignUpRequest;
import com.datatable.blogs.exception.UserException;
import com.datatable.blogs.modal.Role;
import com.datatable.blogs.modal.Users;
import com.datatable.blogs.repository.UserRepository;
import com.datatable.blogs.response.JwtAuthenticationResponse;
import com.datatable.blogs.userservices.AuthenticationService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

	// work on restcontroller
//	@PostMapping("/signup")
//	public ResponseEntity<Users> signup(@RequestBody SignUpRequest signUpRequest) throws UserException {
//
//		return ResponseEntity.ok(authenticationService.signup(signUpRequest));
//
//	}

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

	@PostMapping("/signinxxxxxxxxxx")
	public String generateToken(@ModelAttribute SignInRequest signInRequest, HttpServletResponse response,
			HttpSession session) {
		try {
			JwtAuthenticationResponse jwtResponse = authenticationService.signin(signInRequest);

			// Create and set the JWT token cookie
			Cookie tokenCookie = new Cookie("token", jwtResponse.getToken());
			tokenCookie.setMaxAge(60 * 60 * 24); // 1 day
			tokenCookie.setHttpOnly(true); // Prevent JavaScript access
			tokenCookie.setPath("/"); // Accessible throughout the application
//			tokenCookie.setSecure(true);
			response.addCookie(tokenCookie);

			// Optionally, set a refresh token if needed
			Cookie refreshTokenCookie = new Cookie("refreshToken", jwtResponse.getRefreshToken());
			refreshTokenCookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
			refreshTokenCookie.setHttpOnly(true);
			refreshTokenCookie.setPath("/");
//			refreshTokenCookie.setSecure(true);

			response.addCookie(refreshTokenCookie);

			// fetching role,

			String jwtString = jwtResponse.getToken();

			Users users = authenticationService.findUserUsingJwt(jwtString);

			for (Role role : users.getRoles()) {
				String roleName = role.getName().name(); // This will give you the enum name as a string
				System.out.println("Role: " + roleName);

				// session.setAttribute("roles", roleName);
			}

			return "redirect:/";
		} catch (Exception e) {
			session.setAttribute("msg", "Invalid credentials");
			return "redirect:/signin";
		}
	}

	@PostMapping("/signin")
	public String login(String username, String password, Model model, HttpServletRequest request) {
		Optional<Users> userOptional = userRepository.findByEmail(username);

		if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
			Users user = userOptional.get();

			System.out.println("user" + user);

			Collection<GrantedAuthority> authorities = user.getRoles().stream()
					.map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
					authorities);

			System.out.println("auth" + authToken);

			// Set authentication to the SecurityContext
			SecurityContextHolder.getContext().setAuthentication(authToken);

			HttpSession session = request.getSession(true);
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

			return "redirect:/";
		}

		model.addAttribute("error", "Invalid username or password");
		return "signin";
	}

	// Rest Controller
//	@PostMapping("/signin")
//	public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SignInRequest signInRequest) {
//
//		return ResponseEntity.ok(authenticationService.signin(signInRequest));
//	}
}
