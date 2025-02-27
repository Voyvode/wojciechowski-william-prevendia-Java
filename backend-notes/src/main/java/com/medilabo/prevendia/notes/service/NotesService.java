package com.medilabo.prevendia.notes.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medilabo.prevendia.notes.model.Note;
import com.medilabo.prevendia.notes.repository.NotesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A service class providing functionality to manage notes about patients.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotesService {

	private final NotesRepository repository;

	public Note createNote(Note note) {
		log.info("Creating new note");
		return repository.save(note);
	}

	public List<Note> getNotesByPatId(Long patId) {
		log.info("Listing notes about patient {}", patId);
		return repository.findByPatId(patId);
	}

}