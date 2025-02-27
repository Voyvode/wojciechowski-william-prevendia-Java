package com.medilabo.prevendia.notes.controller;

import java.util.List;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.medilabo.prevendia.notes.model.Note;
import com.medilabo.prevendia.notes.service.NotesService;

import static org.springframework.http.HttpStatus.CREATED;

/**
 * Controller for managing notes about patients.
 */
@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Slf4j
@Validated
public class NotesController {

	private final NotesService notesService;

	@GetMapping("/{patId}")
	public List<Note> getNotesByPatientId(@PathVariable Long patId) {
		log.info("Request to list notes about patient {}", patId);
		return notesService.getNotesByPatId(patId);
	}

	@PostMapping
	@ResponseStatus(CREATED)
	public Note createNote(@Valid @RequestBody Note note) {
		log.info("Request to create a new note about patient {}", note.getPatId());
		return notesService.createNote(note);
	}

}