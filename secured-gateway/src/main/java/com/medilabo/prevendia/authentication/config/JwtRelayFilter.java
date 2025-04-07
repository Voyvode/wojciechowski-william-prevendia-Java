package com.medilabo.prevendia.authentication.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Global filter implementing JWT relay functionality in the API Gateway.
 */
@Component
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
		return chain.filter(exchange);
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
