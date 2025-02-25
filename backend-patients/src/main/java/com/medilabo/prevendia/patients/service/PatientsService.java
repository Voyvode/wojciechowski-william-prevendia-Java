package com.medilabo.prevendia.patients.service;

import java.util.List;

import com.medilabo.prevendia.patients.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.medilabo.prevendia.patients.model.Patient;
import com.medilabo.prevendia.patients.repository.PatientsRepository;

/**
 * A service class providing functionality to manage patient records.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientsService {

	private final PatientsRepository repository;

	public Patient createPatient(Patient patient) {
		log.info("Creating new patient");
		return repository.save(patient);
	}

	public List<Patient> getAllPatients() {
		log.info("Listing all patients");
		return repository.findAll(Sort.by("lastname"));
	}

	public Patient getPatientById(Long id) {
		log.info("Getting patient {}", id);
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No patient found with id " + id));
	}

	public Patient updatePatient(Long id, Patient patient) {
		log.info("Updating patient {}", id);
		return repository.findById(id).map(existingPatient -> {
			patient.setId(id);
			return repository.save(patient);
		}).orElseThrow(() -> new ResourceNotFoundException("No patient found with id " + id));
	}

	public void deletePatient(Long id) {
		log.info("Deleting patient {}", id);
		var patient = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No patient found with id " + id));
		repository.delete(patient);
	}

}
