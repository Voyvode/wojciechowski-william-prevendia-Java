package com.medilabo.prevendia.frontend.dto;

public record AuthenticationRequest(
		String username,
		String password
) { }
