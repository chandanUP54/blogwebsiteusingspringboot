package com.datatable.blogs.response;

import lombok.Data;

@Data
public class JwtAuthenticationResponse {
	private String token;
	private String refreshToken;
}
