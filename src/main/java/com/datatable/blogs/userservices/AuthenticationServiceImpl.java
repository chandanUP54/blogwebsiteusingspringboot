package com.datatable.blogs.userservices;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.datatable.blogs.dto.SignInRequest;
import com.datatable.blogs.dto.SignUpRequest;
import com.datatable.blogs.exception.UserException;
import com.datatable.blogs.modal.Role;
import com.datatable.blogs.modal.RoleName;
import com.datatable.blogs.modal.Users;
import com.datatable.blogs.repository.RoleRepository;
import com.datatable.blogs.repository.UserRepository;
import com.datatable.blogs.response.JwtAuthenticationResponse;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private RoleRepository roleRepository;
	

	public Users signup(SignUpRequest signUpRequest) throws UserException {

		System.out.println("inside signup-->>" + signUpRequest);

		String username = signUpRequest.getEmail();
		Optional<Users> optionalUser = userRepository.findByEmail(username);

		if (optionalUser.isPresent()) {
			throw new UserException("Username is already taken");
		}

		Users newUser = new Users();
		newUser.setEmail(username);
		newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

		System.out.println("here-->>" + newUser);

		Role userRole = roleRepository.findByName(RoleName.ROLE_USER);
//	    Role userRole = roleRepository.findByName(RoleName.ROLE_ADMIN);
		if (userRole == null) {
			userRole = new Role();
			userRole.setName(RoleName.ROLE_USER);
//	        userRole.setName(RoleName.ROLE_ADMIN); 

			roleRepository.save(userRole); // Save the role first
		}

		newUser.setRoles(Set.of(userRole)); // Now set the user role

		return userRepository.save(newUser);
	}
	
	
	@Override
	public JwtAuthenticationResponse signin(SignInRequest signInRequest) {

		System.out.println("signin------>>>" + signInRequest);

		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));

		var workers = userRepository.findByEmail(signInRequest.getUsername())
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

		Users users = userRepository.findByEmail(userEmail).orElseThrow();

		if (users == null) {
			throw new UserException("user not found with email: " + userEmail);
		}

		return users;

	}

}
