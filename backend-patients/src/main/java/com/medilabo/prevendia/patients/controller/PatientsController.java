package com.medilabo.prevendia.patients.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.medilabo.prevendia.patients.model.Patient;
import com.medilabo.prevendia.patients.service.PatientsService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

/**
 * Controller for managing patients records.
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PatientsController {

	private final PatientsService patientsService;

	@PostMapping
	@ResponseStatus(CREATED)
	public Patient createPatient(@Valid @RequestBody Patient patient) {
		log.info("Request to create a new patient");
		return patientsService.createPatient(patient);
	}

	@GetMapping
	@ResponseStatus(OK)
	public List<Patient> getAllPatients() {
		log.info("Request to list all patients");
		return patientsService.getAllPatients();
	}

	@GetMapping("/{id}")
	@ResponseStatus(OK)
	public Patient getPatientById(@PathVariable Long id) {
		log.info("Request to get patient by id: {}", id);
		return patientsService.getPatientById(id);
	}

	@PutMapping("/{id}")
	@ResponseStatus(OK)
	public Patient updatePatient(@PathVariable Long id, @Valid @RequestBody Patient patient) {
		log.info("Request to update patient {}", id);
		return patientsService.updatePatient(id, patient);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(NO_CONTENT)
	public void deletePatient(@PathVariable Long id) {
		log.info("Request to delete patient {}", id);
		patientsService.deletePatient(id);
	}

}
