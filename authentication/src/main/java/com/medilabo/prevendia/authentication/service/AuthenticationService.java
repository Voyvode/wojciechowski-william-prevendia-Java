package com.medilabo.prevendia.authentication.service;

import java.util.HashMap;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.medilabo.prevendia.authentication.dto.AuthenticationRequest;
import com.medilabo.prevendia.authentication.dto.AuthenticationResponse;
import com.medilabo.prevendia.authentication.dto.TokenValidationRequest;
import com.medilabo.prevendia.authentication.dto.TokenValidationResponse;
import com.medilabo.prevendia.authentication.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		var user = userRepository.findByUsername(request.username())
				.orElseThrow(() -> new BadCredentialsException("Nom d'utilisateur ou mot de passe incorrect"));

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new BadCredentialsException("Nom d'utilisateur ou mot de passe incorrect");
		}

		var claims = new HashMap<String, Object>();
		claims.put("roles", user.getRoles());

		var token = jwtService.generateToken(user.getUsername(), claims);
		return new AuthenticationResponse(token, user.getUsername(), user.getRoles());
	}

	public TokenValidationResponse validateToken(TokenValidationRequest request) {
		if (jwtService.validateToken(request.token())) {
			var username = jwtService.extractUsername(request.token());
			var user = userRepository.findByUsername(username)
					.orElse(null);

			if (user != null) {
				return new TokenValidationResponse(true, username, user.getRoles());
			}
		}

		return new TokenValidationResponse(false, null, null);
	}

}