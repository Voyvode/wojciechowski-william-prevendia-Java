package com.medilabo.prevendia.authentication.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

public record ApiError(LocalDateTime time, HttpStatus code, String message, String exception) { }
