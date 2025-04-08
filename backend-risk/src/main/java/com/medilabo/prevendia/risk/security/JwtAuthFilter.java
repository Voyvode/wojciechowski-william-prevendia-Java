package com.medilabo.prevendia.risk.security;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									@NonNull HttpServletResponse response,
									@NonNull FilterChain filterChain) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String requestURI = request.getRequestURI();
		log.debug("Processing request: {} {}", request.getMethod(), requestURI);

		if (authHeader == null) {
			log.warn("No Authorization header found for request: {}", requestURI);
			response.setStatus(SC_UNAUTHORIZED);
			response.getWriter().write("Authorization header is missing");
			return;
		}

		if (!authHeader.startsWith("Bearer ")) {
			log.warn("Invalid Authorization header format for request: {}", requestURI);
			response.setStatus(SC_UNAUTHORIZED);
			response.getWriter().write("Authorization header must start with 'Bearer '");
			return;
		}

		String jwt = authHeader.substring(7);
		if (!jwtUtil.validateToken(jwt)) {
			log.warn("Invalid JWT token for request: {}", requestURI);
			response.setStatus(SC_UNAUTHORIZED);
			response.getWriter().write("Invalid or expired token");
			return;
		}

		String username = jwtUtil.extractUsername(jwt);
		Set<String> roles = jwtUtil.extractRoles(jwt);
		log.debug("Username: {}, Roles: {}", username, roles);

		List<SimpleGrantedAuthority> authorities = roles.stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
		log.debug("Authorities: {}", authorities);

		var authentication = new UsernamePasswordAuthenticationToken(
				username, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		log.debug("JWT token validated successfully for request: {}", requestURI);
		filterChain.doFilter(request, response);
	}
} 