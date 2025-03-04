package com.medilabo.prevendia.frontend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.medilabo.prevendia.frontend.dto.NoteDTO;
import com.medilabo.prevendia.frontend.service.NotesService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotesController {

	private final NotesService notesService;

	@PostMapping("/patients/{id}/notes/add")
	public String addNote(@PathVariable("id") Long id, @Valid NoteDTO noteDTO, BindingResult result, Model model) {
		if (result.hasErrors()) {
			log.warn("Note form errors: {}", result.getAllErrors());
			model.addAttribute("note", noteDTO);
			return "patients/notes";
		}
		log.info("Adding note about patient {}", id);
		notesService.addNote(id, noteDTO);
		return "redirect:/";
	}

	@GetMapping("/patients/{id}/notes")
	public String getPatientNotes(@PathVariable("id") Long id, Model model) {
		log.info("Fetching notes about patient {}", id);

		var fullProfile = notesService.getPatientNotes(id);

		model.addAttribute("patient", fullProfile.getPatient());
		model.addAttribute("notes", fullProfile.getNotes());
		model.addAttribute("risk", fullProfile.getRisk());

		return "patients/notes";
	}


}
