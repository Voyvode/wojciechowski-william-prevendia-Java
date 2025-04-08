package com.medilabo.prevendia.authentication.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Global filter implementing JWT relay functionality in the API Gateway.
 */
@Component
@Slf4j
public class JwtRelayFilter implements GlobalFilter, Ordered {

	/**
	 * Processes each request passing through the gateway.
	 *
	 * @param exchange the current server exchange
	 * @param chain the filter chain
	 * @return Mono completing the filter
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION);

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			log.debug("Relaying JWT token to downstream service: {}", exchange.getRequest().getPath().value());
			log.info("Relaying JWT token to downstream service: {}", exchange.getRequest().getPath().value());
			
			ServerWebExchange mutatedExchange = exchange.mutate()
					.request(exchange.getRequest().mutate()
							.header(AUTHORIZATION, authHeader)
							.build())
					.build();
			
			return chain.filter(mutatedExchange);
		} else {
			log.warn("No JWT token found in request to: {}", exchange.getRequest().getPath().value());
			return chain.filter(exchange);
		}
	}

	/**
	 * Defines filter execution order in the chain.
	 *
	 * @return the filter order value (1)
	 */
	@Override
	public int getOrder() {
		return 1;
	}

}
