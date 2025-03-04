package com.medilabo.prevendia.frontend.controller;

import java.util.List;

import com.medilabo.prevendia.frontend.service.PatientsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.medilabo.prevendia.frontend.dto.PatientDTO;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

	private final PatientsService patientsService;

	@GetMapping("/")
	public String index(Model model) {
		log.info("Displaying patient list");
		List<PatientDTO> patients = patientsService.getPatients();
		model.addAttribute("patients", patients);
		return "index";
	}

}
