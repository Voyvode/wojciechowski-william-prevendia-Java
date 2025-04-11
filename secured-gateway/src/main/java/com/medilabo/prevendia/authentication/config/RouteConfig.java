package com.medilabo.prevendia.authentication.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration des routes pour l'API Gateway.
 * Définit les routes vers les différents micro-services.
 */
@Configuration
public class RouteConfig {

    /**
     * Configure les routes vers les différents micro-services.
     * 
     * @param builder Le builder de routage
     * @return Le RouteLocator configuré
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("patients-service", r -> r
                        .path("/api/patients/**")
                        .uri("http://backend-patients:8081"))
                .route("notes-service", r -> r
                        .path("/api/notes/**")
                        .uri("http://backend-notes:8082"))
                .route("risk-service", r -> r
                        .path("/api/risk/**")
                        .uri("http://backend-risk:8083"))
                .route("frontend-service", r -> r
                        .path("/**")
                        .uri("http://frontend:8090"))
                .build();
    }

}
