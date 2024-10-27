package com.datatable.blogs.userservices;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.datatable.blogs.modal.Users;
import com.datatable.blogs.repository.UserRepository;

@Configuration
public class UserInfoUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository ourUserRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Users> user = ourUserRepo.findByEmail(username);
		System.out.println("user from user info details service: --->> " + user);
		return user.map(UserInfoDetails::new).orElseThrow(() -> new UsernameNotFoundException("User Does Not Exist"));
	}
}
