package com.datatable.blogs.userservices;

import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;

import com.datatable.blogs.modal.Users;

public interface JwtService {
	// eske teen kam user nikalana , token generate karna , token valid karna
	String extractUsername(String token);

	String generateToken(Users workers);

	boolean isTokenValid(String token, UserDetails userDetails);

	public String generateRefreshToken(Map<String, Object> extraClaims, Users workers);
}
