package com.medilabo.prevendia.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FeignConfig {

	@Bean
	public RequestInterceptor bearerTokenRequestInterceptor(HttpSession session) {
		return requestTemplate -> {
			String token = (String) session.getAttribute("token");
			log.info("Bearer token avant envoi requête: {}", token);
			if (token != null && !token.isEmpty()) {
				requestTemplate.header("Authorization", "Bearer " + token);
				log.info("Token ajouté à l'en-tête de la requête");
			} else {
				log.warn("Aucun token disponible pour la requête");
			}
		};
	}

}

