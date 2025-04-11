package com.medilabo.prevendia.authentication.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing an authentication request.
 *
 * @param username the username of the user attempting to authenticate
 * @param password the password of the user attempting to authenticate
 */
public record AuthenticationRequest(
		@NotBlank
		String username,
		
		@NotBlank
		String password
) { }
