package com.medilabo.prevendia.gateway;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;

import reactor.test.StepVerifier;

@SpringBootTest
class GatewayRoutesTests {

	@Autowired
	private RouteLocator routeLocator;

	@Test
	void verifyRoutes() {
		StepVerifier.create(routeLocator.getRoutes())
				.expectNextMatches(route ->
						route.getId().equals("patients-service")
								&& route.getUri().toString().equals("http://backend-patients:8081")
								&& route.getPredicate().toString().contains("/api/patients/**"))
				.expectNextMatches(route ->
						route.getId().equals("notes-service")
								&& route.getUri().toString().equals("http://backend-notes:8082")
								&& route.getPredicate().toString().contains("/api/notes/**"))
				.expectNextMatches(route ->
						route.getId().equals("risk-service")
								&& route.getUri().toString().equals("http://backend-risk:8083")
								&& route.getPredicate().toString().contains("/api/risk/**"))
				.expectNextMatches(route ->
						route.getId().equals("frontend-service")
								&& route.getUri().toString().equals("http://frontend:8090")
								&& route.getPredicate().toString().contains("/**"))
				.expectComplete()
				.verify();
	}

}
