package com.medilabo.prevendia.authentication.service;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import com.medilabo.prevendia.authentication.dto.AuthenticationRequest;
import com.medilabo.prevendia.authentication.dto.AuthenticationResponse;
import com.medilabo.prevendia.authentication.model.User;
import com.medilabo.prevendia.authentication.repository.UserRepository;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtService jwtService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthenticationService authenticationService;

	private static final String USERNAME = "testUser";
	private static final String PASSWORD = "password";
	private static final String ENCODED_PASSWORD = "encodedPassword";
	private static final String TOKEN = "jwtToken";
	private static final String SHOWN_NAME = "Test User";
	private static final Set<String> ROLES = Set.of("ROLE_USER", "ROLE_ADMIN");

	private User user;
	private AuthenticationRequest request;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setUsername(USERNAME);
		user.setPassword(ENCODED_PASSWORD);
		user.setShownName(SHOWN_NAME);
		user.setRoles(ROLES);

		request = new AuthenticationRequest(USERNAME, PASSWORD);
	}

	@Test
	void authenticate_WithValidCredentials_ShouldReturnTokenAndUserInfo() {
		when(userRepository.findByUsername(USERNAME)).thenReturn(Mono.just(user));
		when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
		when(jwtService.generateToken(USERNAME)).thenReturn(Mono.just(TOKEN));

		Mono<AuthenticationResponse> result = authenticationService.authenticate(request);

		StepVerifier.create(result)
				.expectNextMatches(response ->
						response.authenticated() &&
								TOKEN.equals(response.token()) &&
								USERNAME.equals(response.username()) &&
								SHOWN_NAME.equals(response.shownName()) &&
								ROLES.equals(response.roles()))
				.verifyComplete();
	}

	@Test
	void authenticate_WithInvalidPassword_ShouldReturnError() {
		when(userRepository.findByUsername(USERNAME)).thenReturn(Mono.just(user));
		when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

		Mono<AuthenticationResponse> result = authenticationService.authenticate(request);

		StepVerifier.create(result)
				.expectNextMatches(response ->
						!response.authenticated() &&
								response.token() == null &&
								response.username() == null &&
								response.shownName() == null &&
								response.roles() == null
				)
				.verifyComplete();
	}

	@Test
	void authenticate_WithNonExistingUser_ShouldReturnError() {
		when(userRepository.findByUsername(USERNAME)).thenReturn(Mono.empty());

		Mono<AuthenticationResponse> result = authenticationService.authenticate(request);

		StepVerifier.create(result)
				.expectNextMatches(response ->
						!response.authenticated() &&
								response.token() == null &&
								response.username() == null &&
								response.shownName() == null &&
								response.roles() == null
				)
				.verifyComplete();
	}

	@Test
	void validateToken_WithValidToken_ShouldReturnTrue() {
		when(jwtService.validateToken(TOKEN)).thenReturn(Mono.just(true));

		Mono<Boolean> result = authenticationService.validateToken(TOKEN);

		StepVerifier.create(result)
				.expectNext(true)
				.verifyComplete();
	}

	@Test
	void validateToken_WithInvalidToken_ShouldReturnFalse() {
		when(jwtService.validateToken(TOKEN)).thenReturn(Mono.just(false));

		Mono<Boolean> result = authenticationService.validateToken(TOKEN);

		StepVerifier.create(result)
				.expectNext(false)
				.verifyComplete();
	}

} 