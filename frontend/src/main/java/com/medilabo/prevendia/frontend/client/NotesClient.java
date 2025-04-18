package com.medilabo.prevendia.frontend.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.medilabo.prevendia.frontend.dto.NoteDTO;

@FeignClient(name = "notesClient", url = "http://secured-gateway:8080")
public interface NotesClient {

	@GetMapping("/api/notes/{id}")
	List<NoteDTO> getNotesAboutPatient(@PathVariable("id") Long id);

	@PostMapping("/api/notes")
	NoteDTO addNoteAboutPatient(@RequestBody NoteDTO note);

}
