package com.medilabo.prevendia.authentication.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import com.medilabo.prevendia.authentication.dto.AuthenticationRequest;
import com.medilabo.prevendia.authentication.dto.AuthenticationResponse;
import com.medilabo.prevendia.authentication.service.AuthenticationService;

/**
 * REST controller handling authentication operations.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authService;

	/**
	 * Authenticates a user based on provided credentials.
	 *
	 * @param request the authentication request containing username and password
	 * @return a Mono with ResponseEntity containing JWT token if authentication successful
	 */
	@PostMapping("/login")
	public Mono<ResponseEntity<AuthenticationResponse>> login(@RequestBody AuthenticationRequest request) {
		return authService.authenticate(request)
				.map(ResponseEntity::ok);
	}

	/**
	 * Validates a JWT token.
	 *
	 * @param token the JWT token to validate
	 * @return a Mono with ResponseEntity containing boolean result of token validation
	 */
	@PostMapping("/validate")
	public Mono<ResponseEntity<Boolean>> validateToken(@RequestBody String token) {
		return authService.validateToken(token)
				.map(ResponseEntity::ok);
	}

}
