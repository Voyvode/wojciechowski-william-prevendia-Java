package com.medilabo.prevendia.patients.model;

import java.time.LocalDate;

import lombok.Data;

import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

/**
 * A patient entity mapped to a database.
 */
@Entity
@Data
public class Patient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String firstname;

	@NotBlank
	private String lastname;

	@NotNull
	@Past
	private LocalDate birthdate;

	@NotNull
	@Pattern(regexp = "[MF]")
	private String sex;

	@Pattern(regexp = "^\\d{3}-\\d{3}-\\d{4}$|^$")
	private String phone;

	private String address;

}
