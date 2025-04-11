package com.medilabo.prevendia.frontend.service;

import com.medilabo.prevendia.frontend.dto.PatientFullProfileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.medilabo.prevendia.frontend.client.NotesClient;
import com.medilabo.prevendia.frontend.client.PatientsClient;
import com.medilabo.prevendia.frontend.client.RiskClient;
import com.medilabo.prevendia.frontend.dto.NoteDTO;
import com.medilabo.prevendia.frontend.dto.PatientProfileDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotesService {

	private final PatientsClient patientsClient;
	private final NotesClient notesClient;
	private final RiskClient riskClient;

	public void addNote(Long patientId, NoteDTO noteDTO) {
		var patientLastname = patientsClient.getPatient(patientId).getLastname();

		if (!patientId.equals(noteDTO.getPatId())) {
			noteDTO.setPatId(patientId);
			log.warn("Inconsistent patient id corrected");
		}

		if (!patientLastname.equals(noteDTO.getPatient())) {
			noteDTO.setPatient(patientLastname);
			log.warn("Inconsistent patient name corrected");
		}

		notesClient.addNoteAboutPatient(noteDTO);
	}

	public PatientFullProfileDTO getPatientNotes(Long patientId) {
		var patient = patientsClient.getPatient(patientId);
		var notes = notesClient.getNotesAboutPatient(patientId);
		var profile = new PatientProfileDTO(patient, notes);
		var risk = riskClient.assessRisk(profile);

		return new PatientFullProfileDTO(patient, notes, risk);
	}
	
}
