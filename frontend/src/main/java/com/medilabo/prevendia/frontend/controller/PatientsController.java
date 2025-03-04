package com.medilabo.prevendia.frontend.controller;

import com.medilabo.prevendia.frontend.service.PatientsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.medilabo.prevendia.frontend.dto.PatientDTO;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PatientsController {

	private final PatientsService patientsService;

	@GetMapping("/patients/add")
	public String showAddForm(Model model) {
		log.info("Displaying patient add form");
		model.addAttribute("patient", new PatientDTO());
		return "patients/add";
	}

	@PostMapping("/patients/add")
	public String addPatient(@Valid PatientDTO patientDTO,
							 BindingResult result, Model model) {
		if (result.hasErrors()) {
			log.warn("Add form errors: {}", result.getAllErrors());
			model.addAttribute("patient", patientDTO);
			return "patients/add";
		}
		log.info("Adding new patient");
		patientsService.addPatient(patientDTO);
		return "redirect:/";
	}

	@GetMapping("/patients/{id}")
	public String getPatient(@PathVariable("id") Long id, Model model) {
		log.info("Fetching patient {}", id);
		var patient = patientsService.getPatient(id);
		model.addAttribute("patient", patient);
		return "patients/update";
	}

	@GetMapping("/patients/{id}/update")
	public String showUpdateForm(@PathVariable("id") Long id, Model model) {
		log.info("Displaying update form for patient {}", id);
		var patient = patientsService.getPatient(id);
		model.addAttribute("patient", patient);
		return "patients/update";
	}

	@PostMapping("/patients/{id}/update")
	public String updatePatient(@PathVariable("id") Long id, @Valid PatientDTO patientDTO,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			log.warn("Update form errors: {}", result.getAllErrors());
			model.addAttribute("patient", patientDTO);
			return "patients/update";
		}
		log.info("Updating patient {}", id);
		patientsService.updatePatient(id, patientDTO);
		return "redirect:/";
	}

}