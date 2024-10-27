package com.datatable.blogs.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datatable.blogs.dto.SignInRequest;
import com.datatable.blogs.dto.SignUpRequest;
import com.datatable.blogs.exception.UserException;
import com.datatable.blogs.modal.Users;
import com.datatable.blogs.response.JwtAuthenticationResponse;
import com.datatable.blogs.userservices.AuthenticationService;

@RestController
@RequestMapping
@CrossOrigin("*")
public class AuthenticationController {

	@Autowired
	private AuthenticationService authenticationService;

	@PostMapping("/signup")
	public ResponseEntity<Users> signup(@RequestBody SignUpRequest signUpRequest) throws UserException {

		return ResponseEntity.ok(authenticationService.signup(signUpRequest));

	}

	@PostMapping("/signin")
	public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SignInRequest signInRequest) {

		return ResponseEntity.ok(authenticationService.signin(signInRequest));
	}
}
