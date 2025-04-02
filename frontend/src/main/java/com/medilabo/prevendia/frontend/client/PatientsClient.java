package com.medilabo.prevendia.frontend.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.medilabo.prevendia.frontend.dto.PatientDTO;

@FeignClient(name = "patientsClient", url = "http://localhost:8081")
//@FeignClient(name = "patientsClient", url = "http://backend-patients:8081") TODO
public interface PatientsClient {

	@GetMapping("/api/patients")
	List<PatientDTO> getPatients();

	@PostMapping("/api/patients")
	PatientDTO addPatient(PatientDTO patient);

	@GetMapping("/api/patients/{id}")
	PatientDTO getPatient(@PathVariable("id") Long id);

	@PutMapping("/api/patients/{id}")
	PatientDTO updatePatient(@PathVariable("id") Long id, PatientDTO patient);

}
