package com.medilabo.prevendia.authentication.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medilabo.prevendia.authentication.dto.AuthenticationRequest;
import com.medilabo.prevendia.authentication.dto.AuthenticationResponse;
import com.medilabo.prevendia.authentication.dto.TokenValidationRequest;
import com.medilabo.prevendia.authentication.dto.TokenValidationResponse;
import com.medilabo.prevendia.authentication.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authService;

	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authRequest) {
		return ResponseEntity.ok(authService.authenticate(authRequest));
	}

	@PostMapping("/validate")
	public ResponseEntity<TokenValidationResponse> validate(@RequestBody TokenValidationRequest validationRequest) {
		return ResponseEntity.ok(authService.validateToken(validationRequest));
	}

}
