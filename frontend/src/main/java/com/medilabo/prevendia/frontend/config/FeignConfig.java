package com.medilabo.prevendia.frontend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpSession;

@Configuration
public class FeignConfig {

	@Autowired
	private HttpSession session;

	@Bean
	public RequestInterceptor jwtAuthRequestInterceptor() {
		return template -> {
			String token = (String) session.getAttribute("token");
			if (token != null) {
				template.header("Authorization", "Bearer " + token);
			}
		};
	}

}
