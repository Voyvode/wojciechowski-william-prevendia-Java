package com.medilabo.prevendia.frontend.dto;

import java.time.LocalDate;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

@Data
public class PatientDTO {

	private Long id;

	@NotBlank
	private String firstname;

	@NotBlank
	private String lastname;

	@NotNull
	@Past
	private LocalDate birthdate;

	@NotBlank
	private String sex;

	private String address;

	@Pattern(regexp = "\\d{3}-\\d{3}-\\d{4}")
	private String phone;

}
