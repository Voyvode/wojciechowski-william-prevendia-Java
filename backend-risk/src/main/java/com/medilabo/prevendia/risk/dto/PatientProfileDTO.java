package com.medilabo.prevendia.risk.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record PatientProfileDTO(
		@Positive
		int age, 
		
		@NotBlank
		String sex, 
		
		@NotEmpty
		List<String> notes
) { }
