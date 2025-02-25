package com.medilabo.prevendia.patients;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.medilabo.prevendia.patients.model.Patient;
import com.medilabo.prevendia.patients.repository.PatientsRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientsControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PatientsRepository patientsRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		patientsRepository.deleteAll();
	}

	@Test
	void createNewPatient_Success() throws Exception {
		var patient = new Patient();
		patient.setFirstname("Alice");
		patient.setLastname("Doe");
		patient.setBirthdate(LocalDate.of(1985, 5, 15));
		patient.setSex("F");

		mockMvc.perform(post("/api/patients")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(patient)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstname").value("Alice"))
				.andExpect(jsonPath("$.lastname").value("Doe"));
	}

	@Test
	void createNewPatient_InvalidData_Failure() throws Exception {
		var patient = new Patient();
		// Missing required fields
		patient.setFirstname(""); // Invalid: blank
		patient.setLastname("Test");
		patient.setBirthdate(LocalDate.now().plusDays(1)); // Invalid: future date
		patient.setSex("X"); // Invalid: not M or F

		mockMvc.perform(post("/api/patients")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(patient)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getPatientById_Success() throws Exception {
		var patient = new Patient();
		patient.setFirstname("Marie");
		patient.setLastname("Curie");
		patient.setBirthdate(LocalDate.of(1867, 11, 7));
		patient.setSex("F");

		var savedPatient = patientsRepository.save(patient);

		mockMvc.perform(get("/api/patients/{id}", savedPatient.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstname").value("Marie"))
				.andExpect(jsonPath("$.lastname").value("Curie"));
	}

	@Test
	void testGetPatientById_NotFound() throws Exception {
		mockMvc.perform(get("/api/patients/{id}", 999L))
				.andExpect(status().isNotFound());
	}

	@Test
	void getPatients_Empty_Success() throws Exception {
		mockMvc.perform(get("/api/patients"))
				.andExpect(status().isOk())
				.andExpect(content().json("[]"));
	}

	@Test
	void updatePatient_Success() throws Exception {
		var patient = new Patient();
		patient.setFirstname("Tom");
		patient.setLastname("Smith");
		patient.setBirthdate(LocalDate.of(1990, 3, 20));
		patient.setSex("M");

		var savedPatient = patientsRepository.save(patient);

		savedPatient.setLastname("Brown");

		mockMvc.perform(put("/api/patients/{id}", savedPatient.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(savedPatient)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.lastname").value("Brown"));
	}

	@Test
	void updatePatient_NonExisting_Failure() throws Exception {
		var patient = new Patient();
		patient.setFirstname("Nobody");
		patient.setLastname("Nowhere");
		patient.setBirthdate(LocalDate.of(1990, 1, 1));
		patient.setSex("M");

		mockMvc.perform(put("/api/patients/{id}", 999L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(patient)))
				.andExpect(status().isNotFound());
	}

	@Test
	void deletePatient_Success() throws Exception {
		var patient = new Patient();
		patient.setFirstname("Bob");
		patient.setLastname("Johnson");
		patient.setBirthdate(LocalDate.of(2000, 1, 10));
		patient.setSex("M");

		var savedPatient = patientsRepository.save(patient);

		mockMvc.perform(delete("/api/patients/{id}", savedPatient.getId()))
				.andExpect(status().isNoContent());
	}

	@Test
	void deletePatient_NotFound() throws Exception {
		mockMvc.perform(delete("/api/patients/{id}", 999L))
				.andExpect(status().isNotFound());
	}

}
