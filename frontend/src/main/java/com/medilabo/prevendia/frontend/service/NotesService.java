package com.medilabo.prevendia.frontend.service;

import com.medilabo.prevendia.frontend.dto.PatientFullProfileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import feign.FeignException;

import com.medilabo.prevendia.frontend.client.NotesClient;
import com.medilabo.prevendia.frontend.client.PatientsClient;
import com.medilabo.prevendia.frontend.client.RiskClient;
import com.medilabo.prevendia.frontend.dto.NoteDTO;
import com.medilabo.prevendia.frontend.dto.PatientProfileDTO;
import com.medilabo.prevendia.frontend.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotesService {

	private final PatientsClient patientsClient;
	private final NotesClient notesClient;
	private final RiskClient riskClient;

	public void addNote(Long patientId, NoteDTO noteDTO) {
		try {
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
		} catch (FeignException.NotFound e) {
			log.warn("Patient {} not found", patientId);
			throw new ResourceNotFoundException("Patient " + patientId + " not found");
		} catch (FeignException e) {
			log.error("External error after API call to add a note");
			throw new RuntimeException("Erreur while adding note for patient " + patientId, e);
		}
	}

	public PatientFullProfileDTO getPatientNotes(Long patientId) {
		try {
			var patient = patientsClient.getPatient(patientId);
			var notes = notesClient.getNotesAboutPatient(patientId);
			var profile = new PatientProfileDTO(patient, notes);
			var risk = riskClient.assessRisk(profile);

			return new PatientFullProfileDTO(patient, notes, risk);

		} catch (FeignException.NotFound e) {
			log.warn("Patient inexistant : id={}", patientId);
			throw new ResourceNotFoundException("Patient introuvable avec id=" + patientId);
		} catch (FeignException e) {
			log.error("Erreur API externe : patientId={}", patientId, e);
			throw new RuntimeException("Erreur technique lors de la récupération du patient", e);
		}
	}

}
