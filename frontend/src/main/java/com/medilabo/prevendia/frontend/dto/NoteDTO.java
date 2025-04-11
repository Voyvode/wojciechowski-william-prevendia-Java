package com.medilabo.prevendia.frontend.dto;

import java.time.LocalDate;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

@Data
public class NoteDTO {

	private Long patId;

	private String patient;

	@NotBlank
	private String content;

	@NotNull
	@PastOrPresent
	private LocalDate date;

	public NoteDTO() {
		date = LocalDate.now();
	}

}
