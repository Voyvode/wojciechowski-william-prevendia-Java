package com.medilabo.prevendia.frontend.controller;

import java.util.List;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.medilabo.prevendia.frontend.dto.NoteDTO;
import com.medilabo.prevendia.frontend.dto.PatientDTO;
import lombok.RequiredArgsConstructor;

@Controller
public class FrontendController {

	private final RestTemplate restTemplate;

	public FrontendController(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@GetMapping("/")
	public String index(Model model) {
		String patientsApiUrl = "http://backend-patients:8081/api/patients";

		ResponseEntity<List<PatientDTO>> response = restTemplate.exchange(
				patientsApiUrl,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<>() { }
		);

		List<PatientDTO> patients = response.getBody();

		model.addAttribute("patients", patients);
		return "index";
	}

	@GetMapping("/notes")
	public String notes(Model model) {
		String notesApiUrl = "http://backend-notes:8082/api/notes";

		ResponseEntity<List<NoteDTO>> response = restTemplate.exchange(
				notesApiUrl,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<>() { }
		);

		List<NoteDTO> notes = response.getBody();

		model.addAttribute("notes", notes);
		return "notes";
	}

}
