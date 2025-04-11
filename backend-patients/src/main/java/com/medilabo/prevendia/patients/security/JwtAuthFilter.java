package com.medilabo.prevendia.patients.security;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.medilabo.prevendia.patients.exception.AuthenticationException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,
									@NonNull HttpServletResponse response,
									@NonNull FilterChain filterChain) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String requestURI = request.getRequestURI();
		log.debug("Processing request: {} {}", request.getMethod(), requestURI);

		if (authHeader == null) {
			log.warn("No Authorization header found for request: {}", requestURI);
			throw new AuthenticationException("En-tête d'autorisation absent");
		}

		if (!authHeader.startsWith("Bearer ")) {
			log.warn("Invalid Authorization header format for request: {}", requestURI);
			throw new AuthenticationException("En-tête d'autorisation ne commençant pas par 'Bearer '");
		}

		String jwt = authHeader.substring(7);
		if (!jwtUtil.validateToken(jwt)) {
			log.warn("Invalid JWT token for request: {}", requestURI);
			throw new AuthenticationException("Jeton invalide ou expiré");
		}

		String username = jwtUtil.extractUsername(jwt);
		Set<String> roles = jwtUtil.extractRoles(jwt);
		log.debug("Username: {}, Roles: {}", username, roles);

		List<SimpleGrantedAuthority> authorities = roles.stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
		log.debug("Authorities: {}", authorities);

		var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		log.debug("JWT token validated successfully for request: {}", requestURI);
		filterChain.doFilter(request, response);
	}

}
