package com.medilabo.prevendia.authentication.config;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

import com.medilabo.prevendia.authentication.service.JwtService;

/**
 * WebFilter implementation for JWT-based authentication.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

	private final JwtService jwtService;

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

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return chain.filter(exchange);
		}

		final String jwt = authHeader.substring(7);

		return jwtService.validateToken(jwt)
				.flatMap(isValid -> {
					if (isValid) {
						String username = jwtService.extractUsername(jwt);
						var auth = new UsernamePasswordAuthenticationToken(username, null, List.of());
						return chain.filter(exchange)
								.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
					} else {
						return chain.filter(exchange);
					}
				});
	}

}