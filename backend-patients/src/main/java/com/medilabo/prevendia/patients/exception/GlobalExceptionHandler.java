package com.medilabo.prevendia.patients.exception;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationExceptions(
			MethodArgumentNotValidException ex, HttpServletRequest request) {

		List<String> details = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.toList();

		var apiError = new ApiError(
				BAD_REQUEST.value(),
				"Validation error",
				"Invalid provided data",
				request.getRequestURI(),
				details
		);

		return ResponseEntity.status(BAD_REQUEST).body(apiError);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
		var apiError = new ApiError(
				BAD_REQUEST.value(),
				"Resource not found",
				ex.getMessage(),
				request.getRequestURI()
		);
		return ResponseEntity.status(NOT_FOUND).body(apiError);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
		var error = new ApiError(
				INTERNAL_SERVER_ERROR.value(),
				"Internal server error",
				"Something wrong happened in there...",
				request.getRequestURI()
		);
		return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(error);
	}

}
