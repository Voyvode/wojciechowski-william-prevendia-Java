package com.medilabo.prevendia.notes.model;

import java.time.LocalDate;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import org.bson.types.ObjectId;

/**
 * A note entity mapped to a NoSQL database.
 */
@Data
@Document
public final class Note {

	@Id
	private ObjectId id;

	@Positive
	private Long patId;

	@NotBlank
	private String patient;

	@NotBlank
	private String content;

	@NotNull
	private LocalDate date;

	public Note() {
		id = new ObjectId();
		date = LocalDate.now();
	}

	public Note(Long patId, String patient, String content) {
		this.id = new ObjectId();
		this.date = LocalDate.now();
		this.patId = patId;
		this.patient = patient;
		this.content = content;
	}

}
