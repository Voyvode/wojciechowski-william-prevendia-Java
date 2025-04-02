package com.medilabo.prevendia.frontend.dto;

import java.util.Set;

public record AuthenticationResponse(
		boolean authenticated,
		String token,
		String username,
		Set<String> roles) { }