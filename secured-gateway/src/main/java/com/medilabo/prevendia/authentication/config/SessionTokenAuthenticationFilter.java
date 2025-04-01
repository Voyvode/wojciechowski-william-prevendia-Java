package com.medilabo.prevendia.authentication.config;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.medilabo.prevendia.authentication.service.JwtService;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionTokenAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		log.info("Processing request: {}", request.getRequestURI());

		if (!isValidAuthorizationHeader(authHeader)) {
			filterChain.doFilter(request, response);
			return;
		}

		String jwt = authHeader.substring(7);
		authenticateWithJwt(jwt);

		filterChain.doFilter(request, response);
	}

	private boolean isValidAuthorizationHeader(String authHeader) {
		boolean isValid = authHeader != null && authHeader.startsWith("Bearer ");
		log.debug("Authorization header validity: {}", isValid);
		return isValid;
	}

	private void authenticateWithJwt(String jwt) {
		String username = jwtService.extractUsername(jwt);
		log.debug("Extracted username from token: {}", username);

		// Check user is valid and not already authenticated
		if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
			return;
		}

		if (jwtService.validateToken(jwt)) {
			List<SimpleGrantedAuthority> authorities = extractRoleFromToken(jwt);

			var authToken = new UsernamePasswordAuthenticationToken(
					username,
					null,
					authorities
			);

			SecurityContextHolder.getContext().setAuthentication(authToken);
			log.info("Successfully authenticated user: {}", username);
		} else {
			log.warn("Invalid JWT token for user: {}", username);
		}
	}

	private List<SimpleGrantedAuthority> extractRoleFromToken(String jwt) {
		String role = jwtService.extractRole(jwt);

		if (role == null || role.isEmpty()) {
			log.warn("No roles found in token, access denied");
			throw new AccessDeniedException("Token without role, access denied");
		}

		// Prefix with ROLE_
		String formattedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
		return Collections.singletonList(new SimpleGrantedAuthority(formattedRole));
	}


}
