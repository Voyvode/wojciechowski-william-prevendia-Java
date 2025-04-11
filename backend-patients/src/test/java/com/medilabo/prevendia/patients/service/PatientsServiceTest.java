package com.medilabo.prevendia.patients.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import com.medilabo.prevendia.patients.exception.ResourceNotFoundException;
import com.medilabo.prevendia.patients.model.Patient;
import com.medilabo.prevendia.patients.repository.PatientsRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
class PatientsServiceTest {

	@InjectMocks
	private PatientsService patientsService;

	@Mock
	private PatientsRepository patientsRepository;

	public PatientsServiceTest() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createPatient_Success() {
		var patient = new Patient();
		patient.setFirstname("John");
		patient.setLastname("Doe");
		patient.setBirthdate(LocalDate.of(1990, 1, 1));
		patient.setSex("M");

		when(patientsRepository.save(patient)).thenReturn(patient);

		var result = patientsService.createPatient(patient);

		assertNotNull(result);
		assertEquals("John", result.getFirstname());
		verify(patientsRepository, times(1)).save(patient);
	}

	@Test
	void getAllPatients_Success() {
		var patient1 = new Patient();
		patient1.setLastname("Abc");

		var patient2 = new Patient();
		patient2.setLastname("Xyz");

		when(patientsRepository.findAll(any(Sort.class))).thenReturn(List.of(patient1, patient2));

		var result = patientsService.getAllPatients();

		assertEquals(2, result.size());
		verify(patientsRepository).findAll(any(Sort.class));
	}

	@Test
	void getPatientById_Success() {
		var id = 1L;
		var patient = new Patient();
		patient.setId(id);
		patient.setFirstname("Jean");

		when(patientsRepository.findById(id)).thenReturn(Optional.of(patient));

		var result = patientsService.getPatientById(id);

		assertEquals(id, result.getId());
		assertEquals("Jean", result.getFirstname());
	}

	@Test
	void getPatientById_NotFound() {
		var id = 999L;
		when(patientsRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(Exception.class, () -> patientsService.getPatientById(id));
	}

	@Test
	void updatePatient_Success() {
		var id = 1L;
		var existingPatient = new Patient();
		existingPatient.setId(id);
		existingPatient.setFirstname("Jean");
		existingPatient.setLastname("Dupont");
		existingPatient.setBirthdate(LocalDate.of(1980, 5, 15));
		existingPatient.setSex("M");

		var updatedPatient = new Patient();
		updatedPatient.setFirstname("Jean");
		updatedPatient.setLastname("Martin"); // Nom modifié
		updatedPatient.setBirthdate(LocalDate.of(1980, 5, 15));
		updatedPatient.setSex("M");

		when(patientsRepository.findById(id)).thenReturn(Optional.of(existingPatient));
		when(patientsRepository.save(any(Patient.class))).thenAnswer(invocation -> {
			Patient patientToSave = invocation.getArgument(0);
			patientToSave.setId(id);
			return patientToSave;
		});

		var result = patientsService.updatePatient(id, updatedPatient);

		assertNotNull(result);
		assertEquals(id, result.getId());
		assertEquals("Martin", result.getLastname());
		verify(patientsRepository).findById(id);
		verify(patientsRepository).save(updatedPatient);
	}

	@Test
	void updatePatient_NotFound() {
		var id = 1L;
		var patient = new Patient();
		patient.setFirstname("Jane");

		when(patientsRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> patientsService.updatePatient(id, patient));

		verify(patientsRepository, never()).save(patient);
	}

	@Test
	void deletePatient_NotFound() {
		var id = 999L;
		when(patientsRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> patientsService.deletePatient(id));
		verify(patientsRepository, never()).delete(any());
	}

	@Test
	void deletePatient_Success() {
		var id = 1L;
		var patient = new Patient();
		patient.setId(id);
		patient.setFirstname("Jean");
		patient.setLastname("Dupont");
		
		when(patientsRepository.findById(id)).thenReturn(Optional.of(patient));
		
		patientsService.deletePatient(id);
		
		verify(patientsRepository, times(1)).findById(id);
		verify(patientsRepository, times(1)).delete(patient);
	}

}
