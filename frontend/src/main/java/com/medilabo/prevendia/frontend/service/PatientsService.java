package com.medilabo.prevendia.frontend.service;

import java.util.Comparator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.medilabo.prevendia.frontend.client.PatientsClient;
import com.medilabo.prevendia.frontend.dto.PatientDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientsService {

	private final PatientsClient patientsClient;

	public void addPatient(PatientDTO patientDTO) {
		patientsClient.addPatient(patientDTO);
	}

	public List<PatientDTO> getPatients() {
		List<PatientDTO> patients = patientsClient.getPatients();
		patients.sort(Comparator.comparing(PatientDTO::getLastname));
		return patients;
	}

	public PatientDTO getPatient(Long patientId) {
		return patientsClient.getPatient(patientId);
	}

	public void updatePatient(Long patientId, PatientDTO patientDTO) {
		patientsClient.updatePatient(patientId, patientDTO);
	}

}
