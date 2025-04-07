package com.medilabo.prevendia.authentication.dto;

import java.util.Set;

/**
 * DTO representing the response to an authentication request.
 *
 * @param authenticated whether the authentication was successful
 * @param token the JWT token for the authenticated user
 * @param username the username of the authenticated user
 * @param roles the set of roles assigned to the authenticated user
 */
public record AuthenticationResponse(
		boolean authenticated,
		String token,
		String username,
		String shownName,
		Set<String> roles
) { }
