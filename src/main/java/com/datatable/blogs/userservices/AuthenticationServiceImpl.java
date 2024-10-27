package com.datatable.blogs.userservices;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.datatable.blogs.dto.SignInRequest;
import com.datatable.blogs.dto.SignUpRequest;
import com.datatable.blogs.exception.UserException;
import com.datatable.blogs.modal.Users;
import com.datatable.blogs.repository.UserRepository;
import com.datatable.blogs.response.JwtAuthenticationResponse;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	@Autowired
	private UserRepository workerRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtService jwtService;

	@Override
	public Users signup(SignUpRequest signUpRequest) throws UserException {
	    System.out.println("Inside signup");

	    String email = signUpRequest.getEmail();
	    Optional<Users> optionalUser = workerRepository.findByEmail(email);

	    // Check if the user is present
	    if (optionalUser.isPresent()) {
	        throw new UserException("Email is already used with another account");
	    }

	    Users newUser = new Users();
	    newUser.setEmail(email);
	    newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
	    newUser.setRoles("USER");

	    return workerRepository.save(newUser);
	}


	@Override
	public JwtAuthenticationResponse signin(SignInRequest signInRequest) {

		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));

		var workers = workerRepository.findByEmail(signInRequest.getUsername())
				.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

		System.out.println("workers from auth service impl :->" + workers);

		var token = jwtService.generateToken(workers);
		var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), workers);

		JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
		jwtAuthenticationResponse.setToken(token);
		jwtAuthenticationResponse.setRefreshToken(refreshToken);

		return jwtAuthenticationResponse;

	}

	@Override
	public Users findUserUsingJwt(String jwt) throws UserException {

		String userEmail = jwtService.extractUsername(jwt);// error

		System.out.println("username or email from jwt: " + userEmail);

		Users users = workerRepository.findByEmail(userEmail).orElseThrow();

		if (users == null) {
			throw new UserException("user not found with email: " + userEmail);
		}

		return users;

	}

}
