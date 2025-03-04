package com.medilabo.prevendia.frontend.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import feign.FeignException;

import com.medilabo.prevendia.frontend.client.PatientsClient;
import com.medilabo.prevendia.frontend.dto.PatientDTO;
import com.medilabo.prevendia.frontend.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientsService {

	private final PatientsClient patientsClient;

	public void addPatient(PatientDTO patientDTO) {
		try {
			patientsClient.addPatient(patientDTO);
		} catch (FeignException e) {
			log.error("External API error while adding a new patient");
			throw new RuntimeException("Can't add a new patient", e);
		}
	}

	public List<PatientDTO> getPatients() {
		try {
			return patientsClient.getPatients();
		} catch (FeignException e) {
			log.error("External API error while getting patient list", e);
			throw new RuntimeException("Can't get patient list", e);
		}
	}

	public PatientDTO getPatient(Long patientId) {
		try {
			return patientsClient.getPatient(patientId);
		} catch (FeignException.NotFound e) {
			throw new ResourceNotFoundException("Patient " + patientId + " not found");
		} catch (FeignException e) {
			log.error("External API error while fetching a patient", e);
			throw new RuntimeException("Can't fetch a patient", e);
		}
	}

	public void updatePatient(Long patientId, PatientDTO patientDTO) {
		try {
			patientsClient.updatePatient(patientId, patientDTO);
		}  catch (FeignException.NotFound e) {
			throw new ResourceNotFoundException("Patient " + patientId + " not found");
		} catch (FeignException e) {
			log.error("External API error while adding new note to a patient", e);
			throw new RuntimeException("Can't add new note to a patient", e);
		}
	}

}
