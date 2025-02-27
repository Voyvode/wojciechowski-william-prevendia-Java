package com.medilabo.prevendia.frontend.dto;

public class NoteDTO {

	private Long patId;

	private String patient;

	private String content;

	public Long getPatId() {
		return patId;
	}

	public String getPatient() {
		return patient;
	}

	public String getContent() {
		return content;
	}

	public void setPatId(Long patId) {
		this.patId = patId;
	}

	public void setPatient(String patient) {
		this.patient = patient;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
