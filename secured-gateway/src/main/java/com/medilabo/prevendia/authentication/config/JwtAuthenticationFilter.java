package com.medilabo.prevendia.authentication.config;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

import com.medilabo.prevendia.authentication.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebFilter implementation for JWT-based authentication.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

	private final JwtService jwtService;
	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	/**
	 * Filters incoming web requests to authenticate users with JWT.
	 *
	 * @param exchange the server web exchange
	 * @param chain the web filter chain
	 * @return Mono completing the filter
	 */
	@Override
	@NonNull
	public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
		final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		log.info("Authorization header: {}", authHeader);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return chain.filter(exchange);
		}

		final String jwt = authHeader.substring(7);
		log.info("JWT token: {}", jwt);

		return jwtService.validateToken(jwt)
				.flatMap(isValid -> {
					if (isValid) {
						String username = jwtService.extractUsername(jwt);
						Set<String> roles = jwtService.extractRoles(jwt);
						log.info("Username: {}, Roles: {}", username, roles);
						
						var authorities = roles.stream()
								.map(SimpleGrantedAuthority::new)
								.collect(Collectors.toList());
						log.info("Authorities: {}", authorities);
						
						var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
						
						// Ajouter les informations d'authentification à l'en-tête pour les services backend
						ServerHttpRequest request = exchange.getRequest().mutate()
								.header("X-User-Name", username)
								.header("X-User-Roles", String.join(",", roles))
								.build();
						
						ServerWebExchange mutatedExchange = exchange.mutate().request(request).build();
						
						return chain.filter(mutatedExchange)
								.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
					} else {
						log.warn("Token invalide");
						return chain.filter(exchange);
					}
				});
	}

}