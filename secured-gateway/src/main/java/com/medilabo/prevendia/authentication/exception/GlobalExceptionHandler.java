package com.medilabo.prevendia.authentication.exception;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
@Order(-1)
@Slf4j
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

	@Override
	@NonNull
	public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
		log.error("Unexpected error occurred", ex);
		
		ApiError apiError;
		HttpStatus status;

		if (ex instanceof AuthenticationException) {
			status = UNAUTHORIZED;
			apiError = new ApiError(
					LocalDateTime.now(),
					status.value(),
					"Authentication failed",
					"Échec de l'authentification",
					exchange.getRequest().getPath().value(),
					List.of()
			);
		} else if (ex instanceof AccessDeniedException) {
			status = FORBIDDEN;
			apiError = new ApiError(
					LocalDateTime.now(),
					status.value(),
					"Access denied",
					"Accès refusé",
					exchange.getRequest().getPath().value(),
					List.of()
			);
		} else {
			status = INTERNAL_SERVER_ERROR;
			apiError = new ApiError(
					LocalDateTime.now(),
					status.value(),
					"Internal server error",
					"Une erreur inattendue s'est produite",
					exchange.getRequest().getPath().value(),
					List.of()
			);
		}

		exchange.getResponse().setStatusCode(status);
		exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

		byte[] bytes = apiError.toString().getBytes(StandardCharsets.UTF_8);
		DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

		return exchange.getResponse().writeWith(Mono.just(buffer));
	}

}

