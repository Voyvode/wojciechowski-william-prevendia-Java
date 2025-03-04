package com.medilabo.prevendia.frontend.dto;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Data
public class PatientProfileDTO {

	@Positive
	private int age;

	@Pattern(regexp = "[MF]")
	private String sex;

	@NotNull
	private List<String> notes;

	public PatientProfileDTO(PatientDTO patient, List<NoteDTO> notes) {
		this.age = Period.between(patient.getBirthdate(), LocalDate.now()).getYears();
		this.sex = patient.getSex();
		this.notes = notes.stream().map(NoteDTO::getContent).toList();
	}

}
