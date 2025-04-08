package com.medilabo.prevendia.authentication.config;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION;

/**
 * Security configuration for the authentication service.
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthFilter;

	/**
	 * Configures the security filter chain for HTTP requests.
	 *
	 * @param http the ServerHttpSecurity to configure
	 * @return the configured SecurityWebFilterChain
	 */
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
				.authorizeExchange(exchanges ->
						exchanges
								.pathMatchers("/api/auth/login", "/api/auth/validate").permitAll()
								.pathMatchers("/css/**", "/js/**", "/images/**").permitAll()
								.pathMatchers("/api/patients/**").hasAnyAuthority("ROLE_DOCTOR", "ROLE_ORGANIZER")
								.pathMatchers("/api/notes/**").hasAuthority("ROLE_DOCTOR")
								.pathMatchers("/api/risk/**").hasAuthority("ROLE_DOCTOR")
								.anyExchange().authenticated()
				)
				.addFilterAt(jwtAuthFilter, AUTHENTICATION)
				.build();
	}

	/**
	 * Provides password encoder for secure password storage.
	 *
	 * @return BCrypt password encoder instance
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Configures CORS settings for cross-origin requests.
	 *
	 * @return configured CORS source
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		var config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://frontend:8090"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-User-Name", "X-User-Roles"));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);

		var source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

}
