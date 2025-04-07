package com.medilabo.prevendia.authentication.dto;

/**
 * DTO representing an authentication request.
 *
 * @param username the username of the user attempting to authenticate
 * @param password the password of the user attempting to authenticate
 */
public record AuthenticationRequest(
		String username,
		String password
) { }
