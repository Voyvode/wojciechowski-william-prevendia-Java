package com.medilabo.prevendia.frontend.dto;

import java.util.List;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class PatientFullProfileDTO {

	@NotNull
	private PatientDTO patient;

	@NotNull
	private List<NoteDTO> notes;

	@NotNull
	private RiskDTO risk;

	public PatientFullProfileDTO(PatientDTO patient, List<NoteDTO> notes, RiskDTO risk) {
		this.patient = patient;
		this.notes = notes;
		this.risk = risk;
	}

}
