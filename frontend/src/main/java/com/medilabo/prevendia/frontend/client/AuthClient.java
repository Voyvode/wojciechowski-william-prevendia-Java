package com.medilabo.prevendia.frontend.client;

import com.medilabo.prevendia.frontend.dto.TokenValidationRequest;
import com.medilabo.prevendia.frontend.dto.TokenValidationResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.medilabo.prevendia.frontend.dto.AuthenticationRequest;
import com.medilabo.prevendia.frontend.dto.AuthenticationResponse;

@FeignClient(name = "authClient", url = "http://localhost:8080")
//@FeignClient(name = "authClient", url = "http://secured-gateway:8080") TODO
public interface AuthClient {

	@PostMapping("/api/auth/login")
	AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request);

	@PostMapping("/api/auth/validate")
	TokenValidationResponse validateToken(@RequestBody TokenValidationRequest request);
}

