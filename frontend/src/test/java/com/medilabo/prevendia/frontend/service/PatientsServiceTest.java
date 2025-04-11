package com.medilabo.prevendia.frontend.service;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medilabo.prevendia.frontend.client.PatientsClient;
import com.medilabo.prevendia.frontend.dto.PatientDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientsServiceTest {

    @Mock
    private PatientsClient patientsClient;

    @InjectMocks
    private PatientsService patientsService;

    @Test
    void addPatient_ShouldCallClient() {
        var patientDTO = new PatientDTO();
        patientDTO.setFirstname("Jean");
        patientDTO.setLastname("Dupont");
        patientDTO.setBirthdate(LocalDate.of(1980, 1, 1));
        patientDTO.setSex("M");

        patientsService.addPatient(patientDTO);

        verify(patientsClient, times(1)).addPatient(patientDTO);
    }

    @Test
    void getPatients_ShouldReturnList() {
        var patient1 = new PatientDTO();
        patient1.setId(1L);
        patient1.setFirstname("Jean");
        patient1.setLastname("Dupont");

        var patient2 = new PatientDTO();
        patient2.setId(2L);
        patient2.setFirstname("Marie");
        patient2.setLastname("Martin");

        List<PatientDTO> expectedList = List.of(patient1, patient2);

        when(patientsClient.getPatients()).thenReturn(expectedList);

        List<PatientDTO> result = patientsService.getPatients();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(patientsClient, times(1)).getPatients();
    }

    @Test
    void getPatient_ShouldReturnPatient() {
        Long patientId = 1L;
        var expectedPatient = new PatientDTO();
        expectedPatient.setId(patientId);
        expectedPatient.setFirstname("Jean");
        expectedPatient.setLastname("Dupont");

        when(patientsClient.getPatient(patientId)).thenReturn(expectedPatient);

        var result = patientsService.getPatient(patientId);

        assertNotNull(result);
        assertEquals(patientId, result.getId());
        assertEquals("Jean", result.getFirstname());
        assertEquals("Dupont", result.getLastname());
        verify(patientsClient, times(1)).getPatient(patientId);
    }

    @Test
    void updatePatient_ShouldCallClient() {
        Long patientId = 1L;
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstname("Jean");
        patientDTO.setLastname("Dupont");
        patientDTO.setBirthdate(LocalDate.of(1980, 1, 1));
        patientDTO.setSex("M");

        patientsService.updatePatient(patientId, patientDTO);

        verify(patientsClient, times(1)).updatePatient(patientId, patientDTO);
    }

}
