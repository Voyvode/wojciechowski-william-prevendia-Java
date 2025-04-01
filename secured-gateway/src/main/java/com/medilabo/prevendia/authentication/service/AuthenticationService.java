package com.medilabo.prevendia.authentication.service;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.medilabo.prevendia.authentication.dto.AuthenticationRequest;
import com.medilabo.prevendia.authentication.dto.AuthenticationResponse;
import com.medilabo.prevendia.authentication.dto.TokenValidationRequest;
import com.medilabo.prevendia.authentication.dto.TokenValidationResponse;
import com.medilabo.prevendia.authentication.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Service handling user authentication and JWT token validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	/**
	 * Authenticates a user with provided credentials.
	 *
	 * @param request authentication request containing username and password
	 * @return authentication response with JWT token and user details
	 * @throws BadCredentialsException if username doesn't exist or password doesn't match
	 */
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		log.info("User {} asking for authentication", request.username());

		// Unknown username
		var optionalUser = userRepository.findByUsername(request.username());
		if (optionalUser.isEmpty()) {
			log.info("Authentication failed: username {} not found", request.username());
			return new AuthenticationResponse(false, null, null, null);
		}
		var user = optionalUser.get();

		// Wrong password
		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			log.info("Authentication failed: incorrect password for user {}", request.username());
			return new AuthenticationResponse(false, null, null, null);
		}

		var token = jwtService.generateToken(
				user.getUsername(),
				Map.of("roles", user.getRoles())
		);

		log.info("User {} authenticated successfully", request.username());
		return new AuthenticationResponse(true, token, user.getUsername(), user.getRoles());
	}

	/**
	 * Validates a JWT token and retrieves associated user information.
	 *
	 * @param request token validation request containing the JWT token
	 * @return token validation response with username and roles if valid
	 * @throws BadCredentialsException if token is valid but user not found
	 */
	public TokenValidationResponse validateToken(TokenValidationRequest request) {
		log.info("Checking token");
		if (!jwtService.validateToken(request.token())) {
			log.warn("Invalid token");
			return new TokenValidationResponse(false, null, null);
		}

		var username = jwtService.extractUsername(request.token());
		log.debug("Token is valid for user {}", username);

		var userOptional = userRepository.findByUsername(username);
		if (userOptional.isEmpty()) {
			log.warn("Token is valid but user {} not found", username);
			return new TokenValidationResponse(false, null, null);
		}

		var user = userOptional.get();
		return new TokenValidationResponse(true, username, user.getRoles());

	}

}