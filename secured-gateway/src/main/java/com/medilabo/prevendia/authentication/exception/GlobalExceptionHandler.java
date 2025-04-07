package com.medilabo.prevendia.authentication.exception;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ApiError> handleExpiredJwtException(ExpiredJwtException ex) {
		log.error("JWT token expired", ex);
		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				UNAUTHORIZED,
				"JWT token expired",
				ex.getMessage()
		);
		return new ResponseEntity<>(apiError, UNAUTHORIZED);
	}

	@ExceptionHandler(MalformedJwtException.class)
	public ResponseEntity<ApiError> handleMalformedJwtException(MalformedJwtException ex) {
		log.error("Malformed JWT token", ex);
		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				UNAUTHORIZED,
				"Malformed JWT token",
				ex.getMessage()
		);
		return new ResponseEntity<>(apiError, UNAUTHORIZED);
	}

	@ExceptionHandler(SignatureException.class)
	public ResponseEntity<ApiError> handleSignatureException(SignatureException ex) {
		log.error("Invalid JWT signature", ex);
		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				UNAUTHORIZED,
				"Invalid JWT signature",
				ex.getMessage()
		);
		return new ResponseEntity<>(apiError, UNAUTHORIZED);
	}

	@ExceptionHandler(UnsupportedJwtException.class)
	public ResponseEntity<ApiError> handleUnsupportedJwtException(UnsupportedJwtException ex) {
		log.error("Unsupported JWT token", ex);
		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				UNAUTHORIZED,
				"Unsupported JWT token",
				ex.getMessage()
		);
		return new ResponseEntity<>(apiError, UNAUTHORIZED);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex) {
		log.error("Authentication failed", ex);
		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				UNAUTHORIZED,
				"Authentication failed",
				ex.getMessage()
		);
		return new ResponseEntity<>(apiError, UNAUTHORIZED);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
		log.error("Access denied", ex);
		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				FORBIDDEN,
				"Access denied",
				ex.getMessage()
		);
		return new ResponseEntity<>(apiError, FORBIDDEN);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException ex) {
		log.error("Bad credentials", ex);
		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				UNAUTHORIZED,
				"Bad credentials",
				ex.getMessage()
		);
		return new ResponseEntity<>(apiError, UNAUTHORIZED);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleAllOtherExceptions(Exception ex) {
		log.error("Unexpected error occurred", ex);
		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				INTERNAL_SERVER_ERROR,
				"Une erreur inattendue s'est produite",
				ex.getMessage()
		);
		return new ResponseEntity<>(apiError, INTERNAL_SERVER_ERROR);
	}

}

