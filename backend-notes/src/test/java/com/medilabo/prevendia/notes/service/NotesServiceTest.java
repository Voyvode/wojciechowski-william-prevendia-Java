package com.medilabo.prevendia.notes.service;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.context.ActiveProfiles;

import com.medilabo.prevendia.notes.model.Note;
import com.medilabo.prevendia.notes.repository.NotesRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class NotesServiceTest {

	@Mock
	private NotesRepository repository;

	@InjectMocks
	private NotesService notesService;

	@Test
	void getNotesByPatId_Success() {
		var patId = 1L;
		var note = new Note(patId, "Doe", "Checkup scheduled");
		when(repository.findByPatId(patId)).thenReturn(List.of(note));

		List<Note> notes = notesService.getNotesByPatId(patId);

		assertEquals(1, notes.size());
		assertEquals(patId, notes.getFirst().getPatId());
		verify(repository, times(1)).findByPatId(patId);
	}

	@Test
	void getNotesByPatId_NoNotesFound() {
		Long patId = 99L;
		when(repository.findByPatId(patId)).thenReturn(Collections.emptyList());

		List<Note> notes = notesService.getNotesByPatId(patId);

		assertTrue(notes.isEmpty());
		verify(repository, times(1)).findByPatId(patId);
	}

	@Test
	void createNote_Success() {
		var patId = 1L;
		var note = new Note(patId, "Doe", "Checkup scheduled");
		when(repository.save(note)).thenReturn(note);

		Note savedNote = notesService.createNote(note);

		assertEquals(note.getPatId(), savedNote.getPatId());
		assertEquals(note.getPatient(), savedNote.getPatient());
		assertEquals(note.getContent(), savedNote.getContent());
		verify(repository, times(1)).save(note);
	}

}
