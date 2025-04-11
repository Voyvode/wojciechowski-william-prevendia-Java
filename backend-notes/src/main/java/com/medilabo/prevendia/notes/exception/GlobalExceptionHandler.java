package com.medilabo.prevendia.notes.exception;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationExceptions(
			MethodArgumentNotValidException ex, HttpServletRequest request) {
		log.error("Validation error", ex);
		
		List<String> details = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.toList();

		var apiError = new ApiError(
				BAD_REQUEST.value(),
				"Validation error",
				"Données invalides",
				request.getRequestURI(),
				details
		);

		return ResponseEntity.status(BAD_REQUEST).body(apiError);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleResourceNotFoundException(
			ResourceNotFoundException ex, HttpServletRequest request) {
		log.error("Resource not found", ex);
		
		var apiError = new ApiError(
				NOT_FOUND.value(),
				"Resource not found",
				ex.getMessage(),
				request.getRequestURI()
		);
		
		return ResponseEntity.status(NOT_FOUND).body(apiError);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
		log.error("Unexpected error occurred", ex);
		
		var apiError = new ApiError(
				INTERNAL_SERVER_ERROR.value(),
				"Internal server error",
				"Une erreur inattendue s'est produite",
				request.getRequestURI()
		);
		
		return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(apiError);
	}

}
