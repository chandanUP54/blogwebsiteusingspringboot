package com.datatable.blogs.userservices;

import com.datatable.blogs.dto.SignInRequest;
import com.datatable.blogs.dto.SignUpRequest;
import com.datatable.blogs.exception.UserException;
import com.datatable.blogs.modal.Users;
import com.datatable.blogs.response.JwtAuthenticationResponse;

public interface AuthenticationService {

	public Users signup(SignUpRequest signUpRequest) throws UserException;

	public JwtAuthenticationResponse signin(SignInRequest signInRequest);

	public Users findUserUsingJwt(String jwt) throws UserException;
}