package com.medilabo.prevendia.frontend.dto;

import java.time.LocalDate;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class NoteDTO {

	private Long patId;

	private String patient;

	@NotBlank
	private String content;

	private LocalDate date;

	public NoteDTO() {
		date = LocalDate.now();
	}

}
