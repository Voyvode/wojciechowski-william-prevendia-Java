package com.medilabo.prevendia.frontend.service;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medilabo.prevendia.frontend.client.NotesClient;
import com.medilabo.prevendia.frontend.client.PatientsClient;
import com.medilabo.prevendia.frontend.client.RiskClient;
import com.medilabo.prevendia.frontend.dto.NoteDTO;
import com.medilabo.prevendia.frontend.dto.PatientDTO;
import com.medilabo.prevendia.frontend.dto.PatientFullProfileDTO;
import com.medilabo.prevendia.frontend.dto.PatientProfileDTO;
import com.medilabo.prevendia.frontend.dto.RiskDTO;
import com.medilabo.prevendia.frontend.exception.ResourceNotFoundException;
import com.medilabo.prevendia.frontend.exception.ServiceUnavailableException;

import feign.FeignException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotesServiceTest {

    @Mock
    private PatientsClient patientsClient;

    @Mock
    private NotesClient notesClient;

    @Mock
    private RiskClient riskClient;

    @Mock
    private FeignException.NotFound notFoundException;

    @Mock
    private FeignException otherException;

    @InjectMocks
    private NotesService notesService;

    @Test
    void addNote_Success() {
        Long patientId = 1L;
        String lastname = "Dupont";

        var patientDTO = new PatientDTO();
        patientDTO.setId(patientId);
        patientDTO.setLastname(lastname);

        var noteDTO = new NoteDTO();
        noteDTO.setPatId(patientId);
        noteDTO.setPatient(lastname);
        noteDTO.setContent("Note de test");

        when(patientsClient.getPatient(patientId)).thenReturn(patientDTO);

        notesService.addNote(patientId, noteDTO);

        verify(patientsClient, times(1)).getPatient(patientId);
        verify(notesClient, times(1)).addNoteAboutPatient(noteDTO);
    }

    @Test
    void addNote_FixesInconsistentPatientId() {
        Long patientId = 1L;
        Long incorrectPatientId = 2L;
        String lastname = "Dupont";

        var patientDTO = new PatientDTO();
        patientDTO.setId(patientId);
        patientDTO.setLastname(lastname);

        var noteDTO = new NoteDTO();
        noteDTO.setPatId(incorrectPatientId);
        noteDTO.setPatient(lastname);
        noteDTO.setContent("Note de test");

        when(patientsClient.getPatient(patientId)).thenReturn(patientDTO);

        notesService.addNote(patientId, noteDTO);

        assertEquals(patientId, noteDTO.getPatId());
        verify(patientsClient, times(1)).getPatient(patientId);
        verify(notesClient, times(1)).addNoteAboutPatient(noteDTO);
    }

    @Test
    void addNote_FixesInconsistentPatientName() {
        Long patientId = 1L;
        String lastname = "Dupont";
        String incorrectLastname = "Martin";

        var patientDTO = new PatientDTO();
        patientDTO.setId(patientId);
        patientDTO.setLastname(lastname);

        var noteDTO = new NoteDTO();
        noteDTO.setPatId(patientId);
        noteDTO.setPatient(incorrectLastname);
        noteDTO.setContent("Note de test");

        when(patientsClient.getPatient(patientId)).thenReturn(patientDTO);

        notesService.addNote(patientId, noteDTO);

        assertEquals(lastname, noteDTO.getPatient());
        verify(patientsClient, times(1)).getPatient(patientId);
        verify(notesClient, times(1)).addNoteAboutPatient(noteDTO);
    }

    @Test
    void addNote_PatientNotFound() {
        Long patientId = 1L;
        var noteDTO = new NoteDTO();
        noteDTO.setContent("Note de test");

        when(patientsClient.getPatient(patientId)).thenThrow(notFoundException);

        assertThrows(ResourceNotFoundException.class, () -> notesService.addNote(patientId, noteDTO));
        verify(patientsClient, times(1)).getPatient(patientId);
        verify(notesClient, times(0)).addNoteAboutPatient(any());
    }

    @Test
    void addNote_ServiceUnavailable() {
        Long patientId = 1L;
        var noteDTO = new NoteDTO();
        noteDTO.setContent("Note de test");

        when(patientsClient.getPatient(patientId)).thenThrow(otherException);

        // Act & Assert
        assertThrows(ServiceUnavailableException.class, () -> notesService.addNote(patientId, noteDTO));
        verify(patientsClient, times(1)).getPatient(patientId);
        verify(notesClient, times(0)).addNoteAboutPatient(any());
    }

    @Test
    void getPatientNotes_Success() {
        Long patientId = 1L;
        
        var patientDTO = new PatientDTO();
        patientDTO.setId(patientId);
        patientDTO.setFirstname("Jean");
        patientDTO.setLastname("Dupont");
        patientDTO.setBirthdate(LocalDate.of(1980, 1, 1));
        patientDTO.setSex("M");
        
        var noteDTO = new NoteDTO();
        noteDTO.setPatId(patientId);
        noteDTO.setPatient("Dupont");
        noteDTO.setContent("Note de test");
        
        List<NoteDTO> notesList = List.of(noteDTO);
        var risk = RiskDTO.NONE;
        
        when(patientsClient.getPatient(patientId)).thenReturn(patientDTO);
        when(notesClient.getNotesAboutPatient(patientId)).thenReturn(notesList);
        when(riskClient.assessRisk(any(PatientProfileDTO.class))).thenReturn(risk);
        
        PatientFullProfileDTO result = notesService.getPatientNotes(patientId);
        
        assertNotNull(result);
        assertEquals(patientDTO, result.getPatient());
        assertEquals(notesList, result.getNotes());
        assertEquals(risk, result.getRisk());
        verify(patientsClient, times(1)).getPatient(patientId);
        verify(notesClient, times(1)).getNotesAboutPatient(patientId);
        verify(riskClient, times(1)).assessRisk(any(PatientProfileDTO.class));
    }
    
    @Test
    void getPatientNotes_PatientNotFound() {
        Long patientId = 1L;
        
        when(patientsClient.getPatient(patientId)).thenThrow(notFoundException);
        
        assertThrows(ResourceNotFoundException.class, () -> notesService.getPatientNotes(patientId));
        verify(patientsClient, times(1)).getPatient(patientId);
        verify(notesClient, times(0)).getNotesAboutPatient(any());
        verify(riskClient, times(0)).assessRisk(any());
    }
    
    @Test
    void getPatientNotes_ServiceUnavailable() {
        Long patientId = 1L;
        
        when(patientsClient.getPatient(patientId)).thenThrow(otherException);
        
        assertThrows(ServiceUnavailableException.class, () -> notesService.getPatientNotes(patientId));
        verify(patientsClient, times(1)).getPatient(patientId);
        verify(notesClient, times(0)).getNotesAboutPatient(any());
        verify(riskClient, times(0)).assessRisk(any());
    }

}
