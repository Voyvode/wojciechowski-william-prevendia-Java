package com.medilabo.prevendia.notes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.medilabo.prevendia.notes.model.Note;
import com.medilabo.prevendia.notes.repository.NotesRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class NotesControllerIntegrationTest {

	@Container
	private final static MongoDBContainer mongoContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.mongodb.uri", mongoContainer::getConnectionString);
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private NotesRepository repository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeAll
	static void setUp() {
		mongoContainer.start();
	}

	@AfterAll
	static void tearDown() {
		mongoContainer.stop();
	}

	@AfterEach
	void cleanAfterEach(){
		repository.deleteAll();
	}

	@Test
	void createNote_Success() throws Exception {
		var note = new Note(1L, "Doe", "New note");

		mockMvc.perform(post("/api/notes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(note)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.patId").value(note.getPatId()));

		assertThat(repository.findByPatId(1L)).hasSize(1);
	}

	@Test
	void getNotesByPatientId_NoNotes_Success() throws Exception {
		mockMvc.perform(get("/api/notes/99"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	void getNotesByPatientId_OneNote_Success() throws Exception {
		var note = new Note(1L, "Doe", "New note");

		repository.save(note);

		mockMvc.perform(get("/api/notes/{patId}", note.getPatId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].patId").value(note.getPatId()));
	}

}
