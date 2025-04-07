package com.medilabo.prevendia.authentication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import com.medilabo.prevendia.authentication.dto.AuthenticationRequest;
import com.medilabo.prevendia.authentication.dto.AuthenticationResponse;
import com.medilabo.prevendia.authentication.repository.UserRepository;

/**
 * Service handling user authentication and JWT token validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;

	public Mono<AuthenticationResponse> authenticate(AuthenticationRequest request) {
		String username = request.username();
		return userRepository.findByUsername(username)
				.doOnNext(user -> log.info("Authentication attempt by user {}", username))
				.filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
				.flatMap(user -> jwtService.generateToken(user.getUsername())
						.doOnNext(token -> log.info("JWT token generated for user {}", username))
						.map(token -> new AuthenticationResponse(
								true,
								token,
								user.getUsername(),
								user.getShownName(),
								user.getRoles()
						)))
				.switchIfEmpty(Mono.defer(() -> {
					log.warn("Unknown user or wrong password");
					return Mono.just(new AuthenticationResponse(
							false,
							null,
							null,
							null,
							null
					));
				}));
	}
	public Mono<Boolean> validateToken(String token) {
		return jwtService.validateToken(token)
				.doOnNext(valid -> {
					if (valid) {
						log.info("Token validated");
					} else {
						log.warn("Token rejected");
					}
				})
				.doOnError(error -> log.error("Token validation failed", error));
	}

}